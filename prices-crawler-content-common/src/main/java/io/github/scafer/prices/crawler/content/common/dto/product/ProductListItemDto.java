package io.github.scafer.prices.crawler.content.common.dto.product;

import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductListItemDto extends SearchProductDto {
    private String key;
    private int quantity;
    private boolean historyEnabled;

    public ProductListItemDto(String locale, String catalog, ProductDto product, Map<String, Object> data, int quantity, boolean historyEnabled) {
        super(locale, catalog, product, data);
        this.key = product.getId();
        this.quantity = quantity;
        this.historyEnabled = historyEnabled;
    }
}
