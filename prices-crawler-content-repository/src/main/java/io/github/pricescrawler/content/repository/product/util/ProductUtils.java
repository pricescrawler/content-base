package io.github.pricescrawler.content.repository.product.util;

import io.github.pricescrawler.content.common.dao.product.PriceDao;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Log4j2
@UtilityClass
public class ProductUtils {

    public static List<String> parseEanUpcList(List<String> storedEanUpcList, List<String> eanUpcList) {
        if (storedEanUpcList == null) {
            return eanUpcList;
        }

        if (eanUpcList == null) {
            return storedEanUpcList;
        }

        var uniqueEanUpcSet = new HashSet<>(storedEanUpcList);

        for (String eanUpc : eanUpcList) {
            if (!storedEanUpcList.contains(eanUpc)) {
                uniqueEanUpcSet.add(eanUpc);
            }
        }

        return uniqueEanUpcSet.stream().toList();
    }

    public static List<PriceDao> parsePricesHistory(List<PriceDao> storedPrices, PriceDao priceDao, String timezone) {
        try {
            if (storedPrices == null) {
                return List.of(priceDao);
            }

            if (!storedPrices.isEmpty()) {
                var lastStoredPrice = storedPrices.getLast();

                if (!DateTimeUtils.areDatesOnSameDay(lastStoredPrice.getDate(), priceDao.getDate(), timezone)) {
                    storedPrices.add(priceDao);
                }
            } else {
                storedPrices.add(priceDao);
            }
        } catch (Exception exception) {
            log.error("PricesHistory parser exception: {}", exception.getMessage());
        }

        return storedPrices;
    }

    public static List<String> parseSearchTerms(List<String> storedTerms, String term) {
        if (storedTerms == null && (term == null || term.isBlank())) {
            return new ArrayList<>();
        }

        if (storedTerms == null) {
            return List.of(term);
        }

        if (term == null || term.isBlank()) {
            return storedTerms;
        }

        var searchTermsSet = new HashSet<>(storedTerms);
        searchTermsSet.add(term.toLowerCase().trim());

        return searchTermsSet.stream().toList();
    }
}
