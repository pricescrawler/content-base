package io.github.scafer.prices.crawler.content.common.dto.catalog;

import io.github.scafer.prices.crawler.content.common.dao.catalog.CatalogDao;
import io.github.scafer.prices.crawler.content.common.util.DataMapUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogDto {
    private String id;
    private String name;
    private String baseUrl;
    private String imageUrl;
    private String description;
    private boolean isActive;
    private Map<String, Object> data;

    public CatalogDto(CatalogDao catalog) {
        this.id = catalog.getId();
        this.name = catalog.getName();
        this.baseUrl = catalog.getBaseUrl();
        this.imageUrl = catalog.getImageUrl();
        this.description = catalog.getDescription();
        this.isActive = catalog.isActive();
        this.data = DataMapUtils.getMapPublicKeys(catalog.getData());
    }
}
