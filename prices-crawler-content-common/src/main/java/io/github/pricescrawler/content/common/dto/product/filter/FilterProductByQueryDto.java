package io.github.pricescrawler.content.common.dto.product.filter;

import io.github.pricescrawler.content.common.dto.product.search.SearchQueryDto;
import io.github.pricescrawler.content.common.util.IdUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterProductByQueryDto {
    private String query;
    private String storeId;
    private String composedCatalogKey;
    private Map<String, Object> data;

    public FilterProductByQueryDto(SearchQueryDto searchQuery, String composedCatalogKey) {
        this.query = searchQuery.getQuery();
        this.storeId = IdUtils.parseStoreFromComposedKey(composedCatalogKey);
        this.composedCatalogKey = IdUtils.removeLocaleFromComposedKey(composedCatalogKey);
        this.data = searchQuery.getData();
    }
}
