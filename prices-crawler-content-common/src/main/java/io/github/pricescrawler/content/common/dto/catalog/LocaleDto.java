package io.github.pricescrawler.content.common.dto.catalog;

import io.github.pricescrawler.content.common.dao.catalog.LocaleDao;
import io.github.pricescrawler.content.common.util.DataMapUtils;
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
public class LocaleDto {
    private String id;
    private String name;
    private String imageUrl;
    private String description;
    private boolean isActive;
    private List<CategoryDto> categories;
    private Map<String, Object> data;

    public LocaleDto(LocaleDao locale) {
        this.id = locale.getId();
        this.name = locale.getName();
        this.imageUrl = locale.getImageUrl();
        this.description = locale.getDescription();
        this.isActive = locale.isActive();
        this.data = DataMapUtils.getMapPublicKeys(locale.getData());
    }
}
