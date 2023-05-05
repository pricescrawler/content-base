package io.github.pricescrawler.content.common.dto.catalog;

import io.github.pricescrawler.content.common.dao.catalog.StoreDao;
import io.github.pricescrawler.content.common.util.DataMapUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {
    private String id;
    private String name;
    private boolean isActive;
    private Map<String, Object> data;

    public StoreDto(StoreDao store) {
        this.id = store.getId();
        this.name = store.getName();
        this.isActive = store.isActive();
        this.data = DataMapUtils.getMapPublicKeys(data);
    }
}
