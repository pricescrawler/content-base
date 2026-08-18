package io.github.pricescrawler.content.common.dao.feedback;

import io.github.pricescrawler.content.common.dao.base.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document("feedback")
@EqualsAndHashCode(callSuper = true)
public class FeedbackDao extends Identifiable {
    private String type;
    private String message;
    private String email;
}
