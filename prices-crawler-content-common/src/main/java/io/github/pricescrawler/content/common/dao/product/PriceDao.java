package io.github.pricescrawler.content.common.dao.product;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceDao {
    private String regularPrice;
    private String campaignPrice;
    private String pricePerQuantity;
    private String quantity;
    private String date;
    private Map<String, Object> data;

    public PriceDao(ProductDto product) {
        this.regularPrice = product.getRegularPrice();
        this.campaignPrice = product.getCampaignPrice();
        this.pricePerQuantity = product.getPricePerQuantity();
        this.quantity = product.getQuantity() == null || product.getQuantity().isEmpty() ? product.getName() : product.getQuantity();
        this.date = product.getDate();
        this.data = product.getData();
    }
}
