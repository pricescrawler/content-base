package io.github.scafer.prices.crawler.content.service.product.base;

import io.github.scafer.prices.crawler.content.common.dao.catalog.CatalogDao;
import io.github.scafer.prices.crawler.content.common.dao.catalog.LocaleDao;
import io.github.scafer.prices.crawler.content.common.dto.product.ProductListItemDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductsDto;
import io.github.scafer.prices.crawler.content.repository.catalog.CatalogDataService;
import io.github.scafer.prices.crawler.content.repository.product.ProductDataService;
import io.github.scafer.prices.crawler.content.service.product.ProductService;
import io.github.scafer.prices.crawler.content.service.product.cache.ProductCacheService;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class BaseProductService implements ProductService {
    protected final String localeName;
    protected final String catalogName;
    private final CatalogDataService catalogDataService;
    private final ProductDataService productDatabaseService;
    private final ProductCacheService productCacheService;
    protected LocaleDao locale;
    protected CatalogDao catalog;
    @Value("${prices.crawler.cache.enabled:true}")
    private boolean isCacheEnabled;
    @Value("${prices.crawler.history.enabled:true}")
    private boolean isHistoryEnabled;

    protected BaseProductService(String localeName, String catalogName,
                                 CatalogDataService catalogDataService,
                                 ProductDataService productDatabaseService,
                                 ProductCacheService productCacheService) {
        this.localeName = localeName;
        this.catalogName = catalogName;
        this.catalogDataService = catalogDataService;
        this.productDatabaseService = productDatabaseService;
        this.productCacheService = productCacheService;

        catalogDataService.findLocaleById(localeName)
                .ifPresent(value -> locale = value);

        catalogDataService.findCatalogByIdAndLocaleId(catalogName, localeName)
                .ifPresent(value -> catalog = value);
    }

    protected abstract CompletableFuture<SearchProductsDto> searchItemLogic(String query);

    protected abstract CompletableFuture<SearchProductDto> searchItemByProductUrlLogic(String productUrl);

    protected abstract CompletableFuture<ProductListItemDto> updateItemLogic(ProductListItemDto productListItem);

    @Override
    public Mono<SearchProductsDto> searchProduct(String query) {
        if (isLocaleOrCatalogDisabled()) {
            return Mono.just(new SearchProductsDto(localeName, catalogName, new ArrayList<>(), generateCatalogData()));
        }

        if (productCacheService.isProductListCached(localeName, catalogName, query)) {
            var cache = productCacheService.retrieveProductsList(localeName, catalogName, query);
            return Mono.just(new SearchProductsDto(localeName, catalogName, cache, generateCatalogData()));
        }

        var result = searchItemLogic(query)
                .thenApply(value -> saveProductsToDatabaseAndCache(value, query));

        return Mono.fromFuture(result);
    }

    @Override
    public Mono<SearchProductDto> searchProductByUrl(String productUrl) {
        if (isLocaleOrCatalogDisabled()) {
            return Mono.just(new SearchProductDto(localeName, catalogName, null));
        }

        var result = searchItemByProductUrlLogic(productUrl)
                .thenApply(value -> saveProductToDatabase(value, null));

        return Mono.fromFuture(result);
    }

    @Override
    public Mono<ProductListItemDto> updateProduct(ProductListItemDto productListItem) {
        if (isLocaleOrCatalogDisabled()) {
            return Mono.just(productListItem);
        }

        var result = updateItemLogic(productListItem);
        return Mono.fromFuture(result);
    }

    protected Map<String, Object> generateCatalogData() {
        var displayOptions = new HashMap<String, Object>();

        catalogDataService.findCatalogByIdAndLocaleId(catalogName, localeName).stream().findFirst()
                .ifPresent(value -> displayOptions.put("catalogName", value.getName()));

        displayOptions.put("historyEnabled", isLocaleOrCatalogHistoryEnabled());

        return displayOptions;
    }

    private SearchProductDto saveProductToDatabase(SearchProductDto searchProductDto, String query) {
        if (isHistoryEnabled && isLocaleOrCatalogHistoryEnabled()) {
            var searchResultDto = new SearchProductsDto(localeName, catalogName, List.of(searchProductDto.getProduct()), generateCatalogData());
            CompletableFuture.supplyAsync(() -> productDatabaseService.saveSearchResult(searchResultDto, query));
        }

        return searchProductDto;
    }

    private SearchProductsDto saveProductsToDatabaseAndCache(SearchProductsDto searchProductsDto, String query) {
        if (isHistoryEnabled && isLocaleOrCatalogHistoryEnabled()) {
            searchProductsDto.getProducts().stream()
                    .map(product -> new SearchProductDto(searchProductsDto.getLocale(), searchProductsDto.getCatalog(), product))
                    .forEach(value -> saveProductToDatabase(value, query));
        }

        if (isCacheEnabled && isLocaleOrCatalogCacheEnabled()) {
            productCacheService.storeProductsList(localeName, catalogName, query, searchProductsDto.getProducts());
        }

        return searchProductsDto;
    }

    private boolean isLocaleOrCatalogHistoryEnabled() {
        return (locale == null && catalog == null) ||
                ((locale != null && locale.isHistoryEnabled()) && (catalog != null && catalog.isHistoryEnabled()));
    }

    private boolean isLocaleOrCatalogCacheEnabled() {
        return (locale == null && catalog == null) ||
                ((locale != null && locale.isCacheEnabled()) && (catalog != null && catalog.isCacheEnabled()));
    }

    private boolean isLocaleOrCatalogDisabled() {
        if (locale != null) {
            if (!locale.isActive()) {
                return !locale.isActive();
            }

            if (catalog != null) {
                return !catalog.isActive();
            }
        }

        return false;
    }
}
