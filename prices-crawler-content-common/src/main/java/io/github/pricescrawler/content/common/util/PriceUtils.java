package io.github.pricescrawler.content.common.util;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Parses display price strings scraped from catalog sites (e.g. {@code "1,99 €"},
 * {@code "1.234,56€"}, {@code "€ 3.49"}) into normalized numeric values and currency
 * codes. Display strings remain the source of truth; the numeric fields are derived,
 * best-effort enrichment — {@code null} whenever the string cannot be parsed safely.
 */
@UtilityClass
public class PriceUtils {
    private static final Pattern NUMBER_TOKEN = Pattern.compile("\\d+(?:[.,\\s\\u00A0]\\d+)*");
    private static final Pattern UNIT_TOKEN = Pattern.compile("/\\s*([a-zA-Z]{1,6}\\d?)");
    private static final Map<String, String> UNIT_ALIASES = Map.ofEntries(
            Map.entry("KG", "KG"), Map.entry("KILO", "KG"), Map.entry("KGM", "KG"),
            Map.entry("G", "G"), Map.entry("GR", "G"), Map.entry("GRAMA", "G"),
            Map.entry("L", "L"), Map.entry("LT", "L"), Map.entry("LTR", "L"), Map.entry("LITRO", "L"),
            Map.entry("ML", "ML"), Map.entry("CL", "CL"),
            Map.entry("UN", "UN"), Map.entry("UNI", "UN"), Map.entry("UNID", "UN"), Map.entry("UND", "UN"),
            Map.entry("EA", "UN"), Map.entry("PC", "UN"), Map.entry("PCS", "UN"),
            Map.entry("M", "M"), Map.entry("MT", "M"), Map.entry("M2", "M2"),
            Map.entry("DOSE", "DOSE"), Map.entry("DS", "DOSE"),
            Map.entry("KWH", "KWH"), Map.entry("LAV", "LAV"));

    /**
     * Extracts the first numeric value from a display price string.
     * Handles both decimal-comma (PT/EU) and decimal-dot formats, with optional
     * thousands separators (dot, comma, space or NBSP).
     *
     * @return the parsed value, or {@code null} when absent/unparseable
     */
    public static BigDecimal parseValue(String displayPrice) {
        if (displayPrice == null || displayPrice.isBlank()) {
            return null;
        }

        var matcher = NUMBER_TOKEN.matcher(displayPrice);

        if (!matcher.find()) {
            return null;
        }

        var token = matcher.group().replace(" ", "").replace(" ", "");
        var lastDot = token.lastIndexOf('.');
        var lastComma = token.lastIndexOf(',');

        try {
            if (lastDot >= 0 && lastComma >= 0) {
                // Both separators present: the right-most one is the decimal separator.
                if (lastComma > lastDot) {
                    token = token.replace(".", "").replace(',', '.');
                } else {
                    token = token.replace(",", "");
                }
            } else if (lastComma >= 0) {
                token = normalizeSingleSeparator(token, lastComma, ',');
            } else if (lastDot >= 0) {
                token = normalizeSingleSeparator(token, lastDot, '.');
            }

            return new BigDecimal(token);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Detects the currency from a display price string.
     *
     * @return an ISO 4217 code, or {@code null} when no known symbol/code is present
     */
    public static String parseCurrency(String displayPrice) {
        if (displayPrice == null) {
            return null;
        }

        if (displayPrice.contains("€") || displayPrice.toUpperCase().contains("EUR")) {
            return "EUR";
        }

        if (displayPrice.contains("£") || displayPrice.toUpperCase().contains("GBP")) {
            return "GBP";
        }

        if (displayPrice.contains("$") || displayPrice.toUpperCase().contains("USD")) {
            return "USD";
        }

        return null;
    }

    /**
     * Extracts the numeric value of a unit-price display string (e.g. {@code "3,98
     * €/Kg"} → 3.98). Returns {@code null} when no number is present.
     */
    public static BigDecimal parsePricePerQuantityValue(String pricePerQuantity) {
        return parseValue(pricePerQuantity);
    }

    /**
     * Extracts and normalizes the unit of a unit-price display string (e.g.
     * {@code "3,98 €/Kg"} → {@code "KG"}, {@code "1,99 €/lt"} → {@code "L"}).
     * Unknown units are returned upper-cased as-is; {@code null} when the string has
     * no {@code /unit} part.
     */
    public static String parsePricePerQuantityUnit(String pricePerQuantity) {
        if (pricePerQuantity == null || pricePerQuantity.isBlank()) {
            return null;
        }

        var matcher = UNIT_TOKEN.matcher(pricePerQuantity);

        if (!matcher.find()) {
            return null;
        }

        var unit = matcher.group(1).toUpperCase();

        return UNIT_ALIASES.getOrDefault(unit, unit);
    }

    /**
     * Fills the derived numeric fields of a product ({@code regularPriceValue},
     * {@code campaignPriceValue}, {@code currency}, {@code pricePerQuantityValue},
     * {@code pricePerQuantityUnit}) from its display strings. Existing non-null values
     * are kept, so catalog implementations may set exact values themselves (e.g. from
     * a JSON API) and skip the string parsing.
     */
    public static ProductDto enrichPrices(ProductDto product) {
        if (product == null) {
            return null;
        }

        if (product.getRegularPriceValue() == null) {
            product.setRegularPriceValue(parseValue(product.getRegularPrice()));
        }

        if (product.getCampaignPriceValue() == null) {
            product.setCampaignPriceValue(parseValue(product.getCampaignPrice()));
        }

        if (product.getCurrency() == null) {
            var currency = parseCurrency(product.getRegularPrice());
            product.setCurrency(currency != null ? currency : parseCurrency(product.getCampaignPrice()));
        }

        if (product.getPricePerQuantityValue() == null) {
            product.setPricePerQuantityValue(parsePricePerQuantityValue(product.getPricePerQuantity()));
        }

        if (product.getPricePerQuantityUnit() == null) {
            product.setPricePerQuantityUnit(parsePricePerQuantityUnit(product.getPricePerQuantity()));
        }

        return product;
    }

    /**
     * A separator followed by exactly 3 digits (and no other separator hints) is
     * treated as a thousands separator ("1.234" -> 1234); 1–2 trailing digits mean a
     * decimal separator ("1,99" -> 1.99). More than one occurrence of the same
     * separator always means thousands grouping ("1.234.567").
     */
    private static String normalizeSingleSeparator(String token, int lastIndex, char separator) {
        var digitsAfter = token.length() - lastIndex - 1;
        var occurrences = token.chars().filter(c -> c == separator).count();
        var separatorAsString = String.valueOf(separator);

        if (occurrences > 1 || digitsAfter == 3) {
            return token.replace(separatorAsString, "");
        }

        return token.replace(separator, '.');
    }
}
