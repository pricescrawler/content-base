package io.github.pricescrawler.content.common.dao.changelog;

import io.github.pricescrawler.content.common.dao.base.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document("changelog")
@EqualsAndHashCode(callSuper = true)
public class ChangelogDao extends Identifiable {
    private String date;
    private Map<String, String> title;
    private Map<String, String> description;
}
