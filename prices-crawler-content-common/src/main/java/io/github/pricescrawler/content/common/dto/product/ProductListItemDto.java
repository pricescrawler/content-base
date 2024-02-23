package io.github.pricescrawler.content.common.dto.product;

import io.github.pricescrawler.content.common.dto.product.search.SearchProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductListItemDto extends SearchProductDto {
    private String key;
    private int quantity;
    private boolean historyEnabled;
}
