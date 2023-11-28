package io.github.pricescrawler.content.common.dto.product.cache;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCacheDto {
    private String date;
    private List<ProductDto> products;
}
