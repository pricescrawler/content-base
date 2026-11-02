package io.github.pricescrawler.content.repository.product.util;

import io.github.pricescrawler.content.common.dao.product.PriceDao;
import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

@Log4j2
@UtilityClass
public class ProductUtils {
    /**
     * Default number of search terms kept per product.
     */
    public static final int SEARCH_TERMS_LIMIT = 50;

    public static List<String> parseEanUpcList(List<String> storedEanUpcList, List<String> eanUpcList) {
        if (storedEanUpcList == null) {
            return eanUpcList;
        }

        if (eanUpcList == null) {
            return storedEanUpcList;
        }

        var uniqueEanUpcSet = new HashSet<>(storedEanUpcList);
        uniqueEanUpcSet.addAll(eanUpcList);
        return uniqueEanUpcSet.stream().toList();
    }

    /**
     * Records that the product was seen with this price: adds it to the history when it
     * changed, and moves {@code lastSeen} forward either way.
     */
    public static void addPrice(ProductHistoryDao product, PriceDao price, String timezone) {
        product.setPrices(parsePricesHistory(product.getPrices(), price, timezone));
        product.setLastSeen(latest(product.getLastSeen(), price.getDate()));
    }

    /**
     * Appends the price only when it differs from the last stored one, and at most once a
     * day, so a product seen every day at the same price keeps a single entry.
     */
    public static List<PriceDao> parsePricesHistory(List<PriceDao> storedPrices, PriceDao priceDao, String timezone) {
        try {
            if (storedPrices == null || storedPrices.isEmpty()) {
                return new ArrayList<>(List.of(priceDao));
            }

            var mutablePrices = new ArrayList<>(storedPrices);
            var lastStoredPrice = mutablePrices.getLast();

            if (!lastStoredPrice.hasSamePriceAs(priceDao) &&
                    !DateTimeUtils.areDatesOnSameDay(lastStoredPrice.getDate(), priceDao.getDate(), timezone)) {
                mutablePrices.add(priceDao);
            }

            return mutablePrices;
        } catch (Exception exception) {
            log.error("PricesHistory parser exception", exception);
        }

        return storedPrices;
    }

    public static List<String> parseSearchTerms(List<String> storedTerms, String term) {
        return parseSearchTerms(storedTerms, term, SEARCH_TERMS_LIMIT);
    }

    /**
     * Adds the term (lower-cased, trimmed) as the most recent one and keeps the last
     * {@code limit} distinct terms, oldest first.
     */
    public static List<String> parseSearchTerms(List<String> storedTerms, String term, int limit) {
        var terms = new LinkedHashSet<String>(storedTerms == null ? List.of() : storedTerms);

        if (term != null && !term.isBlank()) {
            var normalized = term.toLowerCase().trim();
            terms.remove(normalized);
            terms.add(normalized);
        }

        var list = new ArrayList<>(terms);
        return limit > 0 && list.size() > limit ? new ArrayList<>(list.subList(list.size() - limit, list.size())) : list;
    }

    private static String latest(String stored, String candidate) {
        if (stored == null) {
            return candidate;
        }

        if (candidate == null) {
            return stored;
        }

        try {
            return ZonedDateTime.parse(candidate).isAfter(ZonedDateTime.parse(stored)) ? candidate : stored;
        } catch (Exception exception) {
            log.warn("Could not compare dates {} and {}", stored, candidate);
            return candidate;
        }
    }
}
