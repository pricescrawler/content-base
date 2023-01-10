package io.github.scafer.prices.crawler.content.common.dto.product.search;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
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
public class SearchProductsDto {
    private String locale;
    private String catalog;
    private List<ProductDto> products;
    private Map<String, Object> data;
}
