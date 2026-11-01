package io.github.pricescrawler.content.mcp;

import java.util.List;

/**
 * Compact view of a catalog for MCP clients: only what is needed to pick catalogs for a
 * search, without images and display metadata.
 *
 * @param id                  catalog id to pass to search_products ({@code <locale>.<catalog>})
 * @param name                display name
 * @param locale              locale id
 * @param categories          categories the catalog belongs to
 * @param stores              active stores; search a single one with {@code <id>#<store id>}
 * @param serverSearchEnabled {@code false} when the catalog is bot-protected and only
 *                            searchable from the browser, so search_products returns nothing
 */
public record CatalogSummaryDto(String id, String name, String locale, List<String> categories,
                                List<StoreSummary> stores, boolean serverSearchEnabled) {

    public record StoreSummary(String id, String name) {
    }
}
