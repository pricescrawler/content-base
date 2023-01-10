package io.github.scafer.prices.crawler.content.common.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String pricePerQuantity;
    private String quantity;
    private String brand;
    private String description;
    private String productUrl;
    private String imageUrl;
    private List<String> eanUpcList;
    private String date;
    private Map<String, Object> data;
}
