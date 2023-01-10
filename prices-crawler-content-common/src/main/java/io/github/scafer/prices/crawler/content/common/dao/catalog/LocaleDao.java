package io.github.scafer.prices.crawler.content.common.dao.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("locales")
public class LocaleDao {
    @Id
    private String id;
    private String name;
    private String imageUrl;
    private String description;
    private boolean isActive = true;
    private boolean isCacheEnabled = true;
    private boolean isHistoryEnabled = true;
    private String created;
    private String updated;
    private Map<String, Object> data;
}
