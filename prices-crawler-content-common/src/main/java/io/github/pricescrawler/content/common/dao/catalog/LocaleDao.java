package io.github.pricescrawler.content.common.dao.catalog;

import io.github.pricescrawler.content.common.dao.base.Identifiable;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document("locale")
@EqualsAndHashCode(callSuper = true)
public class LocaleDao extends Identifiable {
    private String name;
    private String imageUrl;
    private String description;
    @Builder.Default
    private boolean isActive = true;
    @Builder.Default
    private boolean isCacheEnabled = true;
    @Builder.Default
    private boolean isHistoryEnabled = true;
}
