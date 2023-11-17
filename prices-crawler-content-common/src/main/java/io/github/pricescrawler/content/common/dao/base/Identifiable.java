package io.github.pricescrawler.content.common.dao.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Identifiable {
    @Id
    private String id;

    @Version
    @EqualsAndHashCode.Exclude
    private int lockVersion;

    private String created;
    private String updated;
    private Map<String, Object> data;
}
