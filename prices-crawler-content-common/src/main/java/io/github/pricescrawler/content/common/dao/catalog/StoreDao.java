package io.github.pricescrawler.content.common.dao.catalog;

import io.github.pricescrawler.content.common.dao.base.Identifiable;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StoreDao extends Identifiable {
    private String name;
    @Builder.Default
    private boolean isActive = true;
    @Builder.Default
    private boolean isCacheEnabled = true;
    @Builder.Default
    private boolean isHistoryEnabled = true;
}
