package io.github.pricescrawler.content.common.dao.product;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * One entry of a product's price history. An entry is stored when the price changes,
 * and holds from its {@code date} until the next entry.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceDao {
    private String regularPrice;
    private String campaignPrice;
    private BigDecimal regularPriceValue;
    private BigDecimal campaignPriceValue;
    private String currency;
    private String pricePerQuantity;
    private BigDecimal pricePerQuantityValue;
    private String pricePerQuantityUnit;
    private String quantity;
    /**
     * Only in entries stored before 0.8.3; the name lives on the product.
     */
    private String name;
    private String date;
    /**
     * Only in entries stored before 0.8.3; the data map lives on the product.
     */
    private Map<String, Object> data;

    public PriceDao(ProductDto product) {
        this.regularPrice = product.getRegularPrice();
        this.campaignPrice = product.getCampaignPrice();
        this.regularPriceValue = product.getRegularPriceValue();
        this.campaignPriceValue = product.getCampaignPriceValue();
        this.currency = product.getCurrency();
        this.pricePerQuantity = product.getPricePerQuantity();
        this.pricePerQuantityValue = product.getPricePerQuantityValue();
        this.pricePerQuantityUnit = product.getPricePerQuantityUnit();
        this.quantity = product.getQuantity();
        this.date = product.getDate();
    }

    /**
     * True when both entries show the same prices and quantity, whatever their dates.
     * Compares the display strings, which every catalog fills.
     */
    public boolean hasSamePriceAs(PriceDao other) {
        return other != null &&
                Objects.equals(regularPrice, other.regularPrice) &&
                Objects.equals(campaignPrice, other.campaignPrice) &&
                Objects.equals(pricePerQuantity, other.pricePerQuantity) &&
                Objects.equals(quantity, other.quantity);
    }
}
