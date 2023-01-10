package io.github.scafer.prices.crawler.content.common.dto.cache;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsCacheDto {
    private String date;

    private List<ProductDto> products;
}
