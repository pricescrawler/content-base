package io.github.pricescrawler.content.common.dto.product.filter;

import io.github.pricescrawler.content.common.util.IdUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterProductByUrlDto {
    private String url;
    private String storeId;
    private String composedCatalogKey;

    public FilterProductByUrlDto(String url, String composedCatalogKey) {
        this.url = url;
        this.storeId = IdUtils.parseStoreFromComposedKey(composedCatalogKey);
        this.composedCatalogKey = IdUtils.removeLocaleFromComposedKey(composedCatalogKey);
    }
}
