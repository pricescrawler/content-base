package io.github.pricescrawler.content.service.product.base;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import io.github.pricescrawler.content.common.dao.catalog.LocaleDao;
import io.github.pricescrawler.content.common.dao.catalog.StoreDao;
import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByQueryDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByUrlDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductsDto;
import io.github.pricescrawler.content.common.util.PriceUtils;
import io.github.pricescrawler.content.repository.catalog.CatalogDataService;
import io.github.pricescrawler.content.repository.product.ProductDataService;
import io.github.pricescrawler.content.repository.product.history.ProductHistoryDataService;
import io.github.pricescrawler.content.service.product.ProductService;
import io.github.pricescrawler.content.service.product.cache.ProductCacheService;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Log4j2
public abstract class BaseProductService implements ProductService {
    private static final String SEARCH_METRIC = "prices.crawler.product.search";

    protected final String localeId;
    protected final String catalogId;
    private final CatalogDataService catalogDataService;
    private final ProductCacheService productCacheService;
    private final ProductDataService productDataService;
    private final ProductHistoryDataService productHistoryDataService;
    protected volatile Optional<LocaleDao> optionalLocale;
    protected volatile Optional<CatalogDao> optionalCatalog;
    private volatile long lastCatalogDataRefreshMillis;

    @Value("${prices.crawler.cache.enabled:true}")
    private boolean isCacheEnabled;
    @Value("${prices.crawler.history.enabled:true}")
    private boolean isHistoryEnabled;
    @Value("${prices.crawler.history.individual.enabled:true}")
    private boolean isIndividualHistoryEnabled;
    @Value("${prices.crawler.history.aggregated.enabled:true}")
    private boolean isAggregatedHistoryEnabled;
    /**
     * How often (in seconds) the locale/catalog configuration is re-read from the
     * database, so toggles (active, cache, history) applied in the back-office take
     * effect without a restart. {@code 0} disables refreshing (startup snapshot only).
     */
    @Value("${prices.crawler.catalog.data.refresh-seconds:300}")
    private long catalogDataRefreshSeconds;

    protected BaseProductService(String localeId, String catalogId,
                                 CatalogDataService catalogDataService,
                                 ProductDataService productDataService,
                                 ProductCacheService productCacheService,
                                 ProductHistoryDataService productHistoryDataService) {
        this.localeId = localeId;
        this.catalogId = catalogId;
        this.catalogDataService = catalogDataService;
        this.productCacheService = productCacheService;
        this.productDataService = productDataService;
        this.productHistoryDataService = productHistoryDataService;
        this.optionalLocale = catalogDataService.findLocaleById(localeId).blockOptional();
        this.optionalCatalog = catalogDataService.findCatalogByIdAndLocaleId(catalogId, localeId).blockOptional();
        this.lastCatalogDataRefreshMillis = System.currentTimeMillis();
    }

    /**
     * Performs the logic for searching for items by a given query.
     *
     * @param filterProduct the query to use for the search
     * @return Mono of {@link SearchProductsDto} object
     */
    protected abstract Mono<SearchProductsDto> searchItemLogic(FilterProductByQueryDto filterProduct);

    /**
     * Performs the logic for searching for an item by its product URL.
     *
     * @param filterProductByUrl the product URL of the item to search for
     * @return Mono of {@link SearchProductDto} object
     */
    protected abstract Mono<SearchProductDto> searchItemByProductUrlLogic(
            FilterProductByUrlDto filterProductByUrl);

    /**
     * Performs the logic for updating an item.
     *
     * @param productListItem the updated item
     * @return Mono of {@link ProductListItemDto} object
     */
    protected abstract Mono<ProductListItemDto> updateItemLogic(ProductListItemDto productListItem);

    @Override
    public Mono<SearchProductsDto> searchProductByQuery(FilterProductByQueryDto filterProductByQuery) {
        refreshCatalogDataIfStale();

        var query = filterProductByQuery.getQuery();
        var storeId = filterProductByQuery.getStoreId();
        var composedCatalogKey = filterProductByQuery.getComposedCatalogKey();

        if (isLocaleOrCatalogOrStoreDisabled(storeId) || (storeId != null && findStore(storeId).isEmpty())) {
            return Mono.just(new SearchProductsDto(localeId, composedCatalogKey, new ArrayList<>(),
                    generateCatalogData(storeId)));
        }

        return productCacheService.isProductSearchResultCached(localeId, composedCatalogKey, query)
                .flatMap(cached -> {
                    if (cached) {
                        incrementSearchMetric("query", "cached");
                        return productCacheService.retrieveProductSearchResult(localeId, composedCatalogKey, query)
                                .map(cacheResult -> new SearchProductsDto(localeId, composedCatalogKey, cacheResult,
                                        generateCatalogData(storeId)));
                    }
                    return searchItemLogic(filterProductByQuery)
                            .doOnNext(value -> {
                                if (value.getProducts() != null) {
                                    value.getProducts().forEach(PriceUtils::enrichPrices);
                                }
                                incrementSearchMetric("query",
                                        value.getProducts() == null || value.getProducts().isEmpty() ? "empty" : "success");
                            })
                            .flatMap(value -> saveProductsToDatabaseAndCache(value, query, composedCatalogKey, storeId))
                            .onErrorResume(t -> {
                                log.error(t.getMessage());
                                incrementSearchMetric("query", "error");
                                return Mono.just(SearchProductsDto.builder()
                                        .locale(localeId)
                                        .catalog(composedCatalogKey)
                                        .products(List.of())
                                        .data(generateCatalogData(storeId))
                                        .build());
                            });
                });
    }

    @Override
    public Mono<SearchProductDto> searchProductByProductUrl(FilterProductByUrlDto filterProductByUrl) {
        refreshCatalogDataIfStale();

        var productUrl = filterProductByUrl.getUrl();
        var storeId = filterProductByUrl.getStoreId();
        var composedCatalogKey = filterProductByUrl.getComposedCatalogKey();

        if (isLocaleOrCatalogOrStoreDisabled(filterProductByUrl.getStoreId())
                || (storeId != null && findStore(storeId).isEmpty())) {
            return Mono.just(new SearchProductDto(localeId, composedCatalogKey, null));
        }

        return productCacheService.isProductSearchResultByUrl(productUrl)
                .flatMap(cached -> {
                    if (cached) {
                        incrementSearchMetric("url", "cached");
                        return productCacheService.retrieveProductSearchResultByUrl(productUrl)
                                .map(cacheResult -> new SearchProductDto(localeId, composedCatalogKey, cacheResult));
                    }
                    return searchItemByProductUrlLogic(filterProductByUrl)
                            .doOnNext(value -> {
                                PriceUtils.enrichPrices(value.getProduct());
                                incrementSearchMetric("url", value.getProduct() == null ? "empty" : "success");
                            })
                            .flatMap(value -> saveProductToDatabase(value, null, composedCatalogKey, storeId))
                            .onErrorResume(t -> {
                                log.error(t.getMessage());
                                incrementSearchMetric("url", "error");
                                return Mono.just(SearchProductDto.builder().build());
                            });
                });
    }

    @Override
    public Mono<ProductListItemDto> updateProductListItem(ProductListItemDto productListItem) {
        refreshCatalogDataIfStale();

        if (isLocaleOrCatalogOrStoreDisabled(null)) {
            return Mono.just(productListItem);
        }

        return updateItemLogic(productListItem)
                .doOnNext(value -> {
                    PriceUtils.enrichPrices(value.getProduct());
                    incrementSearchMetric("update", "success");
                })
                .onErrorResume(t -> {
                    log.error(t.getMessage());
                    incrementSearchMetric("update", "error");
                    return Mono.just(productListItem);
                });
    }

    /**
     * Re-reads the locale/catalog configuration from the database when the last
     * snapshot is older than {@code prices.crawler.catalog.data.refresh-seconds}, so
     * back-office toggle changes propagate without a restart. The refresh is
     * asynchronous and non-blocking: the current request still uses the previous
     * snapshot, later requests see the updated one. Note that values captured by
     * implementations at construction time (e.g. the catalog base URL) are not
     * refreshed.
     */
    private void refreshCatalogDataIfStale() {
        if (catalogDataRefreshSeconds <= 0) {
            return;
        }

        var now = System.currentTimeMillis();

        if (now - lastCatalogDataRefreshMillis < catalogDataRefreshSeconds * 1000) {
            return;
        }

        lastCatalogDataRefreshMillis = now;

        catalogDataService.findLocaleById(localeId)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .subscribe(value -> optionalLocale = value,
                        t -> log.warn("Failed to refresh locale data for {}: {}", localeId, t.getMessage()));

        catalogDataService.findCatalogByIdAndLocaleId(catalogId, localeId)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .subscribe(value -> optionalCatalog = value,
                        t -> log.warn("Failed to refresh catalog data for {}.{}: {}", localeId, catalogId,
                                t.getMessage()));
    }

    /**
     * Increments the per-catalog search counter. Metrics are published through the
     * global Micrometer registry, which Spring Boot wires to the application registry
     * (e.g. Prometheus) by default. Outcomes: {@code cached}, {@code success},
     * {@code empty} (fetch worked but nothing was parsed — the main signal for broken
     * catalog parsers) and {@code error}.
     */
    private void incrementSearchMetric(String type, String outcome) {
        Metrics.counter(SEARCH_METRIC,
                "locale", localeId,
                "catalog", catalogId,
                "type", type,
                "outcome", outcome).increment();
    }

    protected Map<String, Object> generateCatalogData(String storeId) {
        var displayOptions = new HashMap<String, Object>();

        optionalCatalog.ifPresent(value -> displayOptions.put("catalogName", value.getName()));
        findStore(storeId).ifPresent(value -> displayOptions.put("storeName", value.getName()));
        displayOptions.put("historyEnabled", isLocaleOrCatalogOrStoreHistoryEnabled(storeId));

        return displayOptions;
    }

    private Mono<SearchProductDto> saveProductToDatabase(SearchProductDto searchProductDto, String query,
                                                         String composedCatalogKey, String storeId) {
        if (isHistoryEnabled && isLocaleOrCatalogOrStoreHistoryEnabled(storeId)) {
            if (isAggregatedHistoryEnabled) {
                var searchResultDto = new SearchProductsDto(localeId, composedCatalogKey,
                        List.of(searchProductDto.getProduct()), generateCatalogData(storeId));
                productHistoryDataService.saveSearchResult(searchResultDto, query)
                        .subscribe(null, t -> log.error("Error saving product history: {}", t.getMessage()));
            }

            if (isIndividualHistoryEnabled) {
                productDataService.save(List.of(searchProductDto.getProduct()))
                        .subscribe(null, t -> log.error("Error saving product data: {}", t.getMessage()));
            }
        }

        return Mono.just(searchProductDto);
    }

    private Mono<SearchProductsDto> saveProductsToDatabaseAndCache(SearchProductsDto searchProductsDto, String query,
                                                                   String composedCatalogKey, String storeId) {
        if (isHistoryEnabled && isLocaleOrCatalogOrStoreHistoryEnabled(storeId)) {
            Flux.fromIterable(searchProductsDto.getProducts())
                    .map(product -> new SearchProductDto(searchProductsDto.getLocale(),
                            searchProductsDto.getCatalog(), product))
                    .flatMap(value -> saveProductToDatabase(value, query, composedCatalogKey, storeId))
                    .subscribe(null, t -> log.error("Error saving products: {}", t.getMessage()));
        }

        if (isCacheEnabled && isLocaleOrCatalogOrStoreCacheEnabled(storeId)) {
            productCacheService.cacheProductSearchResult(localeId, composedCatalogKey, query,
                            searchProductsDto.getProducts())
                    .subscribe(null, t -> log.error("Error caching product search result: {}", t.getMessage()));
        }

        return Mono.just(searchProductsDto);
    }

    private boolean isLocaleOrCatalogOrStoreHistoryEnabled(String storeId) {
        var result = (optionalLocale.isEmpty() && optionalCatalog.isEmpty()) ||
                ((optionalLocale.isPresent() && optionalLocale.get().isHistoryEnabled())
                        && (optionalCatalog.isPresent() && optionalCatalog.get().isHistoryEnabled()));

        return findStore(storeId).map(storeDao -> result && storeDao.isHistoryEnabled()).orElse(result);
    }

    private boolean isLocaleOrCatalogOrStoreCacheEnabled(String storeId) {
        var result = (optionalLocale.isEmpty() && optionalCatalog.isEmpty()) ||
                ((optionalLocale.isPresent() && optionalLocale.get().isCacheEnabled())
                        && (optionalCatalog.isPresent() && optionalCatalog.get().isCacheEnabled()));

        return findStore(storeId).map(storeDao -> result && storeDao.isCacheEnabled()).orElse(result);
    }

    private boolean isLocaleOrCatalogOrStoreDisabled(String storeId) {
        if (optionalLocale.isPresent()) {
            if (!optionalLocale.get().isActive()) {
                return true;
            }

            if (optionalCatalog.isPresent()) {
                return !optionalCatalog.get().isActive();
            }

            var store = findStore(storeId);

            if (store.isPresent()) {
                return !store.get().isActive();
            }
        }

        return false;
    }

    private Optional<StoreDao> findStore(String storeId) {
        return optionalCatalog.flatMap(catalogDao -> catalogDao.getStores()
                .stream().filter(value -> value.getId().equalsIgnoreCase(storeId)).findFirst());
    }
}
