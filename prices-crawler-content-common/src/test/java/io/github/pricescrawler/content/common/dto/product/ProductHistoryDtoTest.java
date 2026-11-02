package io.github.pricescrawler.content.common.dto.product;

import io.github.pricescrawler.content.common.dao.product.PriceDao;
import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductHistoryDtoTest {
    private static final String TIMEZONE = "Europe/Lisbon";

    @Test
    void endsTheHistoryOnLastSeen() {
        var dto = new ProductHistoryDto(product("2026-10-20T10:00:00Z"), TIMEZONE);

        assertEquals(List.of("2026-10-01", "2026-10-10", "2026-10-20"), dates(dto));
        assertEquals("0,99 €", dto.getPrices().getLast().getRegularPrice());
        assertEquals("2026-10-20", dto.getLastSeen());
    }

    @Test
    void noExtraEntryWhenLastSeenIsTheLastChange() {
        var dto = new ProductHistoryDto(product("2026-10-10T18:00:00Z"), TIMEZONE);

        assertEquals(List.of("2026-10-01", "2026-10-10"), dates(dto));
    }

    @Test
    void legacyProductsWithoutLastSeen() {
        var dto = new ProductHistoryDto(product(null), TIMEZONE);

        assertEquals(List.of("2026-10-01", "2026-10-10"), dates(dto));
    }

    @Test
    void rangeKeepsThePriceInEffectWhenItStarts() {
        var dto = new ProductHistoryDto(product("2026-10-20T10:00:00Z"), TIMEZONE)
                .withPrices(ZonedDateTime.parse("2026-10-05T00:00:00Z"), ZonedDateTime.parse("2026-10-15T00:00:00Z"));

        assertEquals(List.of("2026-10-05", "2026-10-10"), dates(dto));
        assertEquals("1,20 €", dto.getPrices().getFirst().getRegularPrice());
    }

    @Test
    void rangeAfterTheLastChange() {
        var dto = new ProductHistoryDto(product("2026-10-20T10:00:00Z"), TIMEZONE)
                .withPrices(ZonedDateTime.parse("2026-10-12T00:00:00Z"), ZonedDateTime.parse("2026-10-30T00:00:00Z"));

        assertEquals(List.of("2026-10-12", "2026-10-20"), dates(dto));
        assertTrue(dto.getPrices().stream().allMatch(price -> price.getRegularPrice().equals("0,99 €")));
    }

    @Test
    void rangeAfterTheProductWasLastSeenIsEmpty() {
        var dto = new ProductHistoryDto(product("2026-10-20T10:00:00Z"), TIMEZONE)
                .withPrices(ZonedDateTime.parse("2026-11-01T00:00:00Z"), ZonedDateTime.parse("2026-11-30T00:00:00Z"));

        assertTrue(dto.getPrices().isEmpty());
    }

    @Test
    void rangeUsesTheLocaleDay() {
        // 23:30 UTC on 9 October is already 10 October in Lisbon (UTC+1).
        var dto = new ProductHistoryDto(product("2026-10-20T10:00:00Z"), TIMEZONE)
                .withPrices(ZonedDateTime.parse("2026-10-09T23:30:00Z"), ZonedDateTime.parse("2026-10-15T00:00:00Z"));

        assertEquals(List.of("2026-10-10"), dates(dto));
    }

    private static List<String> dates(ProductHistoryDto dto) {
        return dto.getPrices().stream().map(PriceDto::getDate).toList();
    }

    private static ProductHistoryDao product(String lastSeen) {
        return ProductHistoryDao.builder()
                .id("pt.demo.1")
                .prices(List.of(
                        PriceDao.builder().regularPrice("1,20 €").date("2026-10-01T10:00:00Z").build(),
                        PriceDao.builder().regularPrice("0,99 €").date("2026-10-10T10:00:00Z").build()))
                .lastSeen(lastSeen)
                .build();
    }
}
