package io.github.pricescrawler.content.mcp;

import io.github.pricescrawler.content.common.dto.catalog.CatalogDto;
import io.github.pricescrawler.content.common.dto.catalog.LocaleDto;
import io.github.pricescrawler.content.common.dto.product.ProductHistoryDto;
import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByQueryDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductsDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchQueryDto;
import io.github.pricescrawler.content.repository.catalog.CatalogDataService;
import io.github.pricescrawler.content.repository.product.history.ProductHistoryDataService;
import io.github.pricescrawler.content.service.catalog.CatalogService;
import io.github.pricescrawler.content.service.product.ProductService;
import io.github.pricescrawler.content.service.product.list.ProductListService;
import io.github.pricescrawler.content.service.product.provider.ProductServiceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Read-only MCP tools over the same services as the REST controllers. Write operations
 * (ingest, parser, list store) are deliberately not exposed.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(McpServerDefaultsEnvironmentPostProcessor.ENABLED_PROPERTY)
public class PricesCrawlerMcpTools {
    static final int MAX_CATALOGS_PER_SEARCH = 10;
    static final int DEFAULT_RESULTS_PER_CATALOG = 10;
    static final int MAX_RESULTS_PER_CATALOG = 50;
    static final int MAX_QUERY_LENGTH = 200;

    private final CatalogService catalogService;
    private final CatalogDataService catalogDataService;
    private final ProductServiceProvider productServiceProvider;
    private final ProductHistoryDataService productHistoryDataService;
    private final ProductListService productListService;

    @McpTool(name = "list_catalogs", title = "List catalogs",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false),
            description = "Lists the active catalogs (shops) that can be searched, with their ids, categories and stores.")
    public Mono<List<CatalogSummaryDto>> listCatalogs(
            @McpToolParam(required = false, description = "Only return catalogs of this locale id (e.g. \"pt\")") String locale) {
        var locales = isBlank(locale) ? catalogService.searchLocales() : catalogService.searchLocaleById(locale.trim()).flux();

        return locales.filter(LocaleDto::isActive)
                .collectList()
                .map(PricesCrawlerMcpTools::toCatalogSummaries);
    }

    @McpTool(name = "search_products", title = "Search products",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = true),
            description = "Searches products by text in one or more catalogs and returns their current prices. "
                    + "Prices are fetched live, so each catalog adds latency.")
    public Mono<List<SearchProductsDto>> searchProducts(
            @McpToolParam(description = "Text to search for, e.g. \"leite meio gordo\"") String query,
            @McpToolParam(description = "Catalog ids from list_catalogs, e.g. [\"pt.continente\"]; "
                    + "append \"#<store id>\" to search one store") List<String> catalogs,
            @McpToolParam(required = false, description = "Maximum products per catalog (default 10, max 50)") Integer limit) {
        if (isBlank(query) || query.length() > MAX_QUERY_LENGTH) {
            return Mono.error(new IllegalArgumentException(
                    "query must have between 1 and %d characters".formatted(MAX_QUERY_LENGTH)));
        }
        if (catalogs == null || catalogs.isEmpty() || catalogs.size() > MAX_CATALOGS_PER_SEARCH) {
            return Mono.error(new IllegalArgumentException(
                    "catalogs must have between 1 and %d entries".formatted(MAX_CATALOGS_PER_SEARCH)));
        }

        int maxResults = limit == null ? DEFAULT_RESULTS_PER_CATALOG : Math.clamp(limit, 1, MAX_RESULTS_PER_CATALOG);
        var searchQuery = SearchQueryDto.builder().query(query.trim()).build();

        return Flux.fromIterable(catalogs)
                .map(String::trim)
                .distinct()
                .flatMap(catalog -> resolveProductService(catalog)
                        .flatMap(service -> service.searchProductByQuery(new FilterProductByQueryDto(searchQuery, catalog))))
                .map(result -> truncate(result, maxResults))
                .collectList();
    }

    @McpTool(name = "get_price_history", title = "Get price history",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false),
            description = "Returns a product with its stored price history. Use the locale, catalog and reference "
                    + "of a product returned by search_products (catalog without the locale prefix).")
    public Mono<ProductHistoryDto> getPriceHistory(
            @McpToolParam(description = "Locale id, e.g. \"pt\"") String locale,
            @McpToolParam(description = "Catalog id without the locale, e.g. \"continente\"") String catalog,
            @McpToolParam(description = "Product reference") String reference,
            @McpToolParam(required = false, description = "Only prices from this ISO-8601 date-time on; needs endDate") String startDate,
            @McpToolParam(required = false, description = "Only prices up to this ISO-8601 date-time; needs startDate") String endDate) {
        ZonedDateTime start;
        ZonedDateTime end;
        try {
            start = parseDate(startDate);
            end = parseDate(endDate);
        } catch (DateTimeParseException e) {
            return Mono.error(new IllegalArgumentException(
                    "startDate and endDate must be ISO-8601 date-times, e.g. 2026-01-01T00:00:00Z"));
        }

        return productHistoryDataService.findProduct(locale, catalog, reference)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "No price history for %s.%s reference %s".formatted(locale, catalog, reference))))
                .flatMap(product -> catalogDataService.findLocaleById(locale)
                        .map(localeDao -> new ProductHistoryDto(product, localeDao.getTimezone()))
                        .defaultIfEmpty(new ProductHistoryDto(product, null))
                        .map(history -> history.withPrices(start, end)));
    }

    @McpTool(name = "find_products_by_barcode", title = "Find products by barcode",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false),
            description = "Finds the same product across catalogs by its EAN/UPC barcode, with each one's price history. "
                    + "Useful to compare prices between shops.")
    public Mono<List<ProductHistoryDto>> findProductsByBarcode(
            @McpToolParam(description = "EAN or UPC barcode, digits only") String eanUpc) {
        if (isBlank(eanUpc) || !eanUpc.trim().matches("\\d{6,14}")) {
            return Mono.error(new IllegalArgumentException("eanUpc must have between 6 and 14 digits"));
        }

        return productHistoryDataService.findProductsByEanUpc(eanUpc.trim())
                .flatMap(product -> catalogDataService.findLocaleById(product.getLocale())
                        .map(localeDao -> new ProductHistoryDto(product, localeDao.getTimezone()))
                        .defaultIfEmpty(new ProductHistoryDto(product, null)))
                .collectList();
    }

    @McpTool(name = "get_shared_list", title = "Get shared list",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false),
            description = "Returns the products of a shopping list shared from the web app, by its share id.")
    public Mono<List<ProductListItemDto>> getSharedList(
            @McpToolParam(description = "Share id of the list") String id) {
        if (isBlank(id)) {
            return Mono.error(new IllegalArgumentException("id is required"));
        }

        return productListService.retrieveProductList(id.trim())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No shared list with id " + id)));
    }

    private Mono<ProductService> resolveProductService(String catalog) {
        return Mono.justOrEmpty(productServiceProvider.findServiceFromCatalog(catalog))
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Unknown catalog %s; use list_catalogs to get valid ids".formatted(catalog))));
    }

    /**
     * Copies the result instead of trimming it in place: it may be the instance held by the cache.
     */
    private static SearchProductsDto truncate(SearchProductsDto result, int maxResults) {
        var products = result.getProducts();
        if (products == null || products.size() <= maxResults) {
            return result;
        }

        return SearchProductsDto.builder()
                .locale(result.getLocale())
                .catalog(result.getCatalog())
                .products(List.copyOf(products.subList(0, maxResults)))
                .data(result.getData())
                .build();
    }

    private static List<CatalogSummaryDto> toCatalogSummaries(List<LocaleDto> locales) {
        // A catalog appears once per category it belongs to; merge those into one entry.
        Map<String, CatalogSummaryDto> catalogs = new LinkedHashMap<>();

        for (var locale : locales) {
            if (locale.getCategories() == null) {
                continue;
            }
            for (var category : locale.getCategories()) {
                if (category.getCatalogs() == null) {
                    continue;
                }
                category.getCatalogs().stream()
                        .filter(CatalogDto::isActive)
                        .forEach(catalog -> catalogs.compute(catalog.getId(), (id, existing) -> existing == null
                                ? newSummary(locale, category.getId(), catalog)
                                : withCategory(existing, category.getId())));
            }
        }

        return List.copyOf(catalogs.values());
    }

    private static CatalogSummaryDto newSummary(LocaleDto locale, String category, CatalogDto catalog) {
        var stores = catalog.getStores() == null ? List.<CatalogSummaryDto.StoreSummary>of() : catalog.getStores().stream()
                .filter(store -> store.isActive())
                .map(store -> new CatalogSummaryDto.StoreSummary(store.getId(), store.getName()))
                .toList();

        return new CatalogSummaryDto(catalog.getId(), catalog.getName(), locale.getId(),
                new ArrayList<>(List.of(category)), stores, !catalog.isClientFetchRequired());
    }

    private static CatalogSummaryDto withCategory(CatalogSummaryDto summary, String category) {
        if (!summary.categories().contains(category)) {
            summary.categories().add(category);
        }
        return summary;
    }

    private static ZonedDateTime parseDate(String value) {
        return isBlank(value) ? null : ZonedDateTime.parse(value.trim());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
