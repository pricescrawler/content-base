package io.github.pricescrawler.content.common.dto.product.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchQueryDto {
    private String[] catalogs;
    private String query;
}
