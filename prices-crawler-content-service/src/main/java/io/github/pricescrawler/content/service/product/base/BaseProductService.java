package io.github.pricescrawler.content.service.product.base;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import io.github.pricescrawler.content.common.dao.catalog.LocaleDao;
import io.github.pricescrawler.content.common.dao.catalog.StoreDao;
import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByQueryDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByUrlDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductsDto;
import io.github.pricescrawler.content.repository.catalog.CatalogDataService;
import io.github.pricescrawler.content.repository.product.ProductDataService;
import io.github.pricescrawler.content.repository.product.history.ProductHistoryDataService;
import io.github.pricescrawler.content.service.product.ProductService;
import io.github.pricescrawler.content.service.product.cache.ProductCacheService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Log4j2
public abstract class BaseProductService implements ProductService {
    protected final String localeId;
    protected final String catalogId;
    private final ProductCacheService productCacheService;
    private final ProductDataService productDataService;
    private final ProductHistoryDataService productHistoryDataService;
    protected Optional<LocaleDao> optionalLocale;
    protected Optional<CatalogDao> optionalCatalog;

    @Value("${prices.crawler.cache.enabled:true}")
    private boolean isCacheEnabled;
    @Value("${prices.crawler.history.enabled:true}")
    private boolean isHistoryEnabled;
    @Value("${prices.crawler.history.individual.enabled:true}")
    private boolean isIndividualHistoryEnabled;
    @Value("${prices.crawler.history.aggregated.enabled:true}")
    private boolean isAggregatedHistoryEnabled;

    protected BaseProductService(String localeId, String catalogId,
                                 CatalogDataService catalogDataService,
                                 ProductDataService productDataService,
                                 ProductCacheService productCacheService,
                                 ProductHistoryDataService productHistoryDataService) {
        this.localeId = localeId;
        this.catalogId = catalogId;
        this.productCacheService = productCacheService;
        this.productDataService = productDataService;
        this.productHistoryDataService = productHistoryDataService;
        this.optionalLocale = catalogDataService.findLocaleById(localeId).blockOptional();
        this.optionalCatalog = catalogDataService.findCatalogByIdAndLocaleId(catalogId, localeId).blockOptional();
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
                        return productCacheService.retrieveProductSearchResult(localeId, composedCatalogKey, query)
                                .map(cacheResult -> new SearchProductsDto(localeId, composedCatalogKey, cacheResult,
                                        generateCatalogData(storeId)));
                    }
                    return searchItemLogic(filterProductByQuery)
                            .flatMap(value -> saveProductsToDatabaseAndCache(value, query, composedCatalogKey, storeId))
                            .onErrorResume(t -> {
                                log.error(t.getMessage());
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
                        return productCacheService.retrieveProductSearchResultByUrl(productUrl)
                                .map(cacheResult -> new SearchProductDto(localeId, composedCatalogKey, cacheResult));
                    }
                    return searchItemByProductUrlLogic(filterProductByUrl)
                            .flatMap(value -> saveProductToDatabase(value, null, composedCatalogKey, storeId))
                            .onErrorResume(t -> {
                                log.error(t.getMessage());
                                return Mono.just(SearchProductDto.builder().build());
                            });
                });
    }

    @Override
    public Mono<ProductListItemDto> updateProductListItem(ProductListItemDto productListItem) {
        if (isLocaleOrCatalogOrStoreDisabled(null)) {
            return Mono.just(productListItem);
        }

        return updateItemLogic(productListItem)
                .onErrorResume(t -> {
                    log.error(t.getMessage());
                    return Mono.just(productListItem);
                });
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
