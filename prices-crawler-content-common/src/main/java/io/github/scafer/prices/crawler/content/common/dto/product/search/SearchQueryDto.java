package io.github.scafer.prices.crawler.content.common.dto.product.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchQueryDto {
    private String[] catalogs;
    private String query;
}
