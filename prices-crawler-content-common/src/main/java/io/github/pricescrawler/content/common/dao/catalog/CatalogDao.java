package io.github.pricescrawler.content.common.dao.catalog;

import io.github.pricescrawler.content.common.dao.base.Identifiable;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document("catalog")
public class CatalogDao extends Identifiable {
    private String name;
    private String baseUrl;
    private String imageUrl;
    private String description;
    private List<String> locales;
    private List<String> categories;
    private List<StoreDao> stores;
    @Builder.Default
    private boolean isActive = true;
    @Builder.Default
    private boolean isCacheEnabled = true;
    @Builder.Default
    private boolean isHistoryEnabled = true;
}
