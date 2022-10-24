package io.github.scafer.prices.crawler.content.common.dto.cache;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductsCacheDto {
    private String date;

    private List<ProductDto> products;
}
