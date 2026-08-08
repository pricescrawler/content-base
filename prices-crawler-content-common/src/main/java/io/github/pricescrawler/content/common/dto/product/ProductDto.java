package io.github.pricescrawler.content.common.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String id;
    private String reference;
    private String name;
    private String regularPrice;
    private String campaignPrice;
    /**
     * Numeric values derived from the display price strings (see
     * {@code PriceUtils.enrichPrices}); {@code null} when not parseable.
     */
    private BigDecimal regularPriceValue;
    private BigDecimal campaignPriceValue;
    /**
     * ISO 4217 currency code derived from the display price strings.
     */
    private String currency;
    private String pricePerQuantity;
    /**
     * Numeric unit price and normalized unit (KG, G, L, ML, UN, …) derived from
     * {@code pricePerQuantity}; {@code null} when not parseable.
     */
    private BigDecimal pricePerQuantityValue;
    private String pricePerQuantityUnit;
    private String quantity;
    private String brand;
    private String description;
    private String productUrl;
    private String imageUrl;
    private List<String> eanUpcList;
    private String date;
    private Map<String, Object> data;
}
