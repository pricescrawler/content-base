package io.github.pricescrawler.content.repository.product.util;

import io.github.pricescrawler.content.common.dao.product.PriceDao;
import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductUtilsTest {
    private static final String TIMEZONE = "Europe/Lisbon";

    @Test
    void anUnchangedPriceIsNotStoredAgain() {
        var prices = ProductUtils.parsePricesHistory(List.of(price("1,20 €", "2026-10-01T10:00:00Z")),
                price("1,20 €", "2026-10-05T10:00:00Z"), TIMEZONE);

        assertEquals(1, prices.size());
        assertEquals("2026-10-01T10:00:00Z", prices.getFirst().getDate());
    }

    @Test
    void aChangedPriceIsStored() {
        var prices = ProductUtils.parsePricesHistory(List.of(price("1,20 €", "2026-10-01T10:00:00Z")),
                price("0,99 €", "2026-10-05T10:00:00Z"), TIMEZONE);

        assertEquals(2, prices.size());
        assertEquals("0,99 €", prices.getLast().getRegularPrice());
    }

    @Test
    void aCampaignEndingCountsAsAChange() {
        var onCampaign = price("1,20 €", "2026-10-01T10:00:00Z");
        onCampaign.setCampaignPrice("0,99 €");

        var prices = ProductUtils.parsePricesHistory(List.of(onCampaign), price("1,20 €", "2026-10-05T10:00:00Z"), TIMEZONE);

        assertEquals(2, prices.size());
    }

    @Test
    void atMostOneEntryADay() {
        var prices = ProductUtils.parsePricesHistory(List.of(price("1,20 €", "2026-10-01T08:00:00Z")),
                price("0,99 €", "2026-10-01T18:00:00Z"), TIMEZONE);

        assertEquals(1, prices.size());
    }

    @Test
    void startsTheHistoryWhenEmpty() {
        assertEquals(1, ProductUtils.parsePricesHistory(null, price("1,20 €", "2026-10-01T10:00:00Z"), TIMEZONE).size());
        assertEquals(1, ProductUtils.parsePricesHistory(List.of(), price("1,20 €", "2026-10-01T10:00:00Z"), TIMEZONE).size());
    }

    @Test
    void addPriceMovesLastSeenForwardOnly() {
        var product = ProductHistoryDao.builder()
                .prices(new ArrayList<>(List.of(price("1,20 €", "2026-10-01T10:00:00Z"))))
                .lastSeen("2026-10-03T10:00:00Z")
                .build();

        ProductUtils.addPrice(product, price("1,20 €", "2026-10-09T10:00:00Z"), TIMEZONE);
        assertEquals("2026-10-09T10:00:00Z", product.getLastSeen());
        assertEquals(1, product.getPrices().size());

        // An older observation (e.g. a merged incident) does not move it back.
        ProductUtils.addPrice(product, price("1,20 €", "2026-10-02T10:00:00Z"), TIMEZONE);
        assertEquals("2026-10-09T10:00:00Z", product.getLastSeen());
    }

    @Test
    void addPriceSetsLastSeenOnLegacyProducts() {
        var product = ProductHistoryDao.builder().prices(new ArrayList<>()).build();
        assertNull(product.getLastSeen());

        ProductUtils.addPrice(product, price("1,20 €", "2026-10-01T10:00:00Z"), TIMEZONE);

        assertEquals("2026-10-01T10:00:00Z", product.getLastSeen());
    }

    @Test
    void searchTermsKeepTheMostRecentOnes() {
        var terms = ProductUtils.parseSearchTerms(List.of("leite", "pão", "queijo"), "  Leite ", 3);
        assertEquals(List.of("pão", "queijo", "leite"), terms);

        terms = ProductUtils.parseSearchTerms(terms, "manteiga", 3);
        assertEquals(List.of("queijo", "leite", "manteiga"), terms);
    }

    @Test
    void searchTermsWithoutATerm() {
        assertEquals(List.of(), ProductUtils.parseSearchTerms(null, " "));
        assertEquals(List.of("leite"), ProductUtils.parseSearchTerms(null, "Leite"));
        assertEquals(List.of("a", "b"), ProductUtils.parseSearchTerms(List.of("a", "b"), null, 0));
    }

    private static PriceDao price(String regularPrice, String date) {
        return PriceDao.builder().regularPrice(regularPrice).date(date).build();
    }
}
