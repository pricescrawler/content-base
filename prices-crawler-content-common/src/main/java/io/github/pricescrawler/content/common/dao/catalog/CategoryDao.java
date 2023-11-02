package io.github.pricescrawler.content.common.dao.catalog;

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
@Document("category")
public class CategoryDao {
    @Id
    private String id;
    private String name;
    private String imageUrl;
    private String description;
    @Builder.Default
    private boolean isActive = true;
    @Builder.Default
    private boolean isCacheEnabled = true;
    @Builder.Default
    private boolean isHistoryEnabled = true;
    private String created;
    private String updated;
    private Map<String, Object> data;
}
