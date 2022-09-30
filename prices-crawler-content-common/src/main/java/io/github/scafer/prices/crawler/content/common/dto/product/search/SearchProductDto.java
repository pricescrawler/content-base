package io.github.scafer.prices.crawler.content.common.dto.product.search;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchProductDto {
    private String locale;
    private String catalog;
    private ProductDto product;
    private Map<String, Object> data;

    public SearchProductDto(String locale, String catalog, ProductDto product) {
        this.locale = locale;
        this.catalog = catalog;
        this.product = product;
    }
}
