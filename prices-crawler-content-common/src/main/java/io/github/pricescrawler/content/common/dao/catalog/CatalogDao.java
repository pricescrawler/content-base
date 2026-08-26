package io.github.pricescrawler.content.common.dao.catalog;

import io.github.pricescrawler.content.common.dao.base.Identifiable;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document("catalog")
@EqualsAndHashCode(callSuper = true)
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
    /**
     * When {@code true}, this catalog's backend never fetches from the site itself —
     * a client (e.g. the web-app, running in a real browser) fetches the page and
     * submits the raw content to {@code ProductContentParserController} for parsing.
     * Meant for catalogs whose bot protection blocks the backend's own datacenter IP.
     */
    @Builder.Default
    private boolean isClientFetchRequired = false;
    /**
     * Path (relative to {@link #baseUrl}) the client should fetch to search this
     * catalog when {@link #isClientFetchRequired} is {@code true}, with a
     * {@code {query}} placeholder for the URL-encoded search term — e.g.
     * {@code "/search?q={query}"}. Unused otherwise.
     */
    private String clientFetchSearchUrlTemplate;
}
