package io.github.pricescrawler.content.common.util;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PriceUtilsTest {

    @ParameterizedTest
    @CsvSource({
            "'1,99 €', 1.99",
            "'0,55€', 0.55",
            "'12,5 €', 12.5",
            "'99.99 €', 99.99",
            "'499.90', 499.90",
            "'1.234,56 €', 1234.56",
            "'1,234.56 $', 1234.56",
            "'1.299 €', 1299",
            "'1.234.567 €', 1234567",
            "'€ 3.49', 3.49",
            "'2 €', 2",
            "'preço: 7,20 € /un', 7.20",
    })
    void parseValue(String input, BigDecimal expected) {
        assertEquals(0, expected.compareTo(PriceUtils.parseValue(input)),
                "Parsing '" + input + "'");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "sem preço", "€"})
    void parseValueReturnsNullWhenNotParseable(String input) {
        assertNull(PriceUtils.parseValue(input));
    }

    @ParameterizedTest
    @CsvSource({
            "'1,99 €', EUR",
            "'EUR 1.99', EUR",
            "'£4.50', GBP",
            "'$ 12.00', USD",
    })
    void parseCurrency(String input, String expected) {
        assertEquals(expected, PriceUtils.parseCurrency(input));
    }

    @Test
    void parseCurrencyReturnsNullWhenUnknown() {
        assertNull(PriceUtils.parseCurrency("1.99"));
        assertNull(PriceUtils.parseCurrency(null));
    }

    @ParameterizedTest
    @CsvSource({
            "'3,98 €/Kg', 3.98, KG",
            "'€3.98/kg', 3.98, KG",
            "'1,99 €/lt', 1.99, L",
            "'(2,50 €/un)', 2.50, UN",
            "'0,89 €/L', 0.89, L",
            "'4,75 €/ml', 4.75, ML",
            "'12,30 € / kg', 12.30, KG",
            "'1,05 €/dose', 1.05, DOSE",
            "'2,20 €/gr', 2.20, G",
            "'0,15 €/unid', 0.15, UN",
    })
    void parsePricePerQuantity(String input, BigDecimal expectedValue, String expectedUnit) {
        assertEquals(0, expectedValue.compareTo(PriceUtils.parsePricePerQuantityValue(input)),
                "Value of '" + input + "'");
        assertEquals(expectedUnit, PriceUtils.parsePricePerQuantityUnit(input),
                "Unit of '" + input + "'");
    }

    @Test
    void parsePricePerQuantityUnitReturnsNullWithoutUnitPart() {
        assertNull(PriceUtils.parsePricePerQuantityUnit("1,99 €"));
        assertNull(PriceUtils.parsePricePerQuantityUnit(null));
        assertNull(PriceUtils.parsePricePerQuantityUnit("  "));
    }

    @Test
    void parsePricePerQuantityUnknownUnitIsReturnedUppercased() {
        assertEquals("CX", PriceUtils.parsePricePerQuantityUnit("5,00 €/cx"));
    }

    @Test
    void enrichPricesFillsDerivedFields() {
        var product = ProductDto.builder()
                .regularPrice("1.234,56 €")
                .campaignPrice("999,90 €")
                .pricePerQuantity("3,98 €/Kg")
                .build();

        PriceUtils.enrichPrices(product);

        assertEquals(0, new BigDecimal("1234.56").compareTo(product.getRegularPriceValue()));
        assertEquals(0, new BigDecimal("999.90").compareTo(product.getCampaignPriceValue()));
        assertEquals("EUR", product.getCurrency());
        assertEquals(0, new BigDecimal("3.98").compareTo(product.getPricePerQuantityValue()));
        assertEquals("KG", product.getPricePerQuantityUnit());
    }

    @Test
    void enrichPricesKeepsExistingValues() {
        var product = ProductDto.builder()
                .regularPrice("1,99 €")
                .regularPriceValue(new BigDecimal("2.49"))
                .currency("USD")
                .build();

        PriceUtils.enrichPrices(product);

        assertEquals(0, new BigDecimal("2.49").compareTo(product.getRegularPriceValue()),
                "Pre-set numeric value must not be overwritten");
        assertEquals("USD", product.getCurrency());
    }

    @Test
    void enrichPricesHandlesNulls() {
        assertNull(PriceUtils.enrichPrices(null));

        var product = new ProductDto();
        PriceUtils.enrichPrices(product);

        assertNull(product.getRegularPriceValue());
        assertNull(product.getCampaignPriceValue());
        assertNull(product.getCurrency());
    }
}
