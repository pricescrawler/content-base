package io.github.scafer.prices.crawler.content.common.dao.catalog;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Document("catalogs")
public class CatalogDao {
    @Id
    private String id;
    private String name;
    private String baseUrl;
    private String imageUrl;
    private String description;
    private List<String> locales;
    private List<String> categories;
    private boolean isActive = true;
    private boolean isCacheEnabled = true;
    private boolean isHistoryEnabled = true;
    private String created;
    private String updated;
    private Map<String, Object> data;
}
