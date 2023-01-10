package io.github.scafer.prices.crawler.content.common.dto.catalog;

import io.github.scafer.prices.crawler.content.common.dao.catalog.CategoryDao;
import io.github.scafer.prices.crawler.content.common.util.DataMapUtils;
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
public class CategoryDto {
    private String id;
    private String name;
    private String imageUrl;
    private String description;
    private boolean isActive;
    private List<CatalogDto> catalogs;
    private Map<String, Object> data;

    public CategoryDto(CategoryDao category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.isActive = category.isActive();
        this.data = DataMapUtils.getMapPublicKeys(category.getData());
    }
}
