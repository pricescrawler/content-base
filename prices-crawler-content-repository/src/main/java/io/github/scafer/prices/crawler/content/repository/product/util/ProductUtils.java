package io.github.scafer.prices.crawler.content.repository.product.util;

import io.github.scafer.prices.crawler.content.common.dao.product.PriceDao;
import io.github.scafer.prices.crawler.content.common.util.DateTimeUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductUtils {

    public static List<String> parseEanUpcList(List<String> storedEanUpcList, List<String> eanUpcList) {
        if (storedEanUpcList == null) {
            return eanUpcList;
        }

        for (String eanUpc : eanUpcList) {
            if (!storedEanUpcList.contains(eanUpc)) {
                storedEanUpcList.add(eanUpc);
            }
        }

        return storedEanUpcList;
    }

    public static List<PriceDao> parsePricesHistory(List<PriceDao> storedPrices, PriceDao priceDao) {
        try {
            if (storedPrices == null) {
                return List.of(priceDao);
            }

            if (!storedPrices.isEmpty()) {
                var lastStoredPrice = storedPrices.get(storedPrices.size() - 1);

                if (!DateTimeUtils.isSameDay(lastStoredPrice.getDate(), priceDao.getDate())) {
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

        var parsedTerm = term.toLowerCase().trim();

        if (storedTerms.stream().noneMatch(value -> value.equals(parsedTerm))) {
            storedTerms.add(parsedTerm);
        }

        return storedTerms;
    }
}
