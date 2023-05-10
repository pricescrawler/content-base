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
import io.github.pricescrawler.content.service.product.ProductService;
import io.github.pricescrawler.content.service.product.cache.ProductCacheService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Log4j2
public abstract class BaseProductService implements ProductService {
    protected final String localeId;
    protected final String catalogId;
    private final ProductDataService productDatabaseService;
    private final ProductCacheService productCacheService;
    protected Optional<LocaleDao> optionalLocale;
    protected Optional<CatalogDao> optionalCatalog;
    @Value("${prices.crawler.cache.enabled:true}")
    private boolean isCacheEnabled;
    @Value("${prices.crawler.history.enabled:true}")
    private boolean isHistoryEnabled;

    protected BaseProductService(String localeId, String catalogId,
                                 CatalogDataService catalogDataService,
                                 ProductDataService productDatabaseService,
                                 ProductCacheService productCacheService) {
        this.localeId = localeId;
        this.catalogId = catalogId;
        this.productDatabaseService = productDatabaseService;
        this.productCacheService = productCacheService;
        this.optionalLocale = catalogDataService.findLocaleById(localeId);
        this.optionalCatalog = catalogDataService.findCatalogByIdAndLocaleId(catalogId, localeId);
    }

    /**
     * Performs the logic for searching for items by a given query.
     *
     * @param filterProduct the query to use for the search
     * @return a CompletableFuture that completes with the {@link SearchProductsDto} object
     */
    protected abstract CompletableFuture<SearchProductsDto> searchItemLogic(FilterProductByQueryDto filterProduct);

    /**
     * Performs the logic for searching for an item by its product URL.
     *
     * @param filterProductByUrl the product URL of the item to search for
     * @return a CompletableFuture that completes with the {@link SearchProductDto} object
     */
    protected abstract CompletableFuture<SearchProductDto> searchItemByProductUrlLogic(
            FilterProductByUrlDto filterProductByUrl);

    /**
     * Performs the logic for updating an item.
     *
     * @param productListItem the updated item
     * @return a CompletableFuture that completes with the updated {@link ProductListItemDto} object
     */
    protected abstract CompletableFuture<ProductListItemDto> updateItemLogic(ProductListItemDto productListItem);

    @Override
    public Mono<SearchProductsDto> searchProductByQuery(FilterProductByQueryDto filterProductByQuery) {
        var query = filterProductByQuery.getQuery();
        var storeId = filterProductByQuery.getStoreId();
        var composedCatalogKey = filterProductByQuery.getComposedCatalogKey();

        if (isLocaleOrCatalogOrStoreDisabled(storeId) || (storeId != null && findStore(storeId).isEmpty())) {
            return Mono.just(new SearchProductsDto(localeId, composedCatalogKey, new ArrayList<>(),
                    generateCatalogData(storeId)));
        }

        if (productCacheService.isProductSearchResultCached(localeId, composedCatalogKey, query)) {
            var cache = productCacheService.retrieveProductSearchResult(localeId, composedCatalogKey, query);
            return Mono.just(new SearchProductsDto(localeId, composedCatalogKey, cache, generateCatalogData(storeId)));
        }

        var result = searchItemLogic(filterProductByQuery)
                .thenApply(value -> saveProductsToDatabaseAndCache(value, query, composedCatalogKey, storeId))
                .exceptionally(t -> {
                            log.error(t.getMessage());
                            return SearchProductsDto.builder()
                                    .locale(localeId)
                                    .catalog(composedCatalogKey)
                                    .products(List.of())
                                    .data(generateCatalogData(storeId))
                                    .build();
                        }
                );

        return Mono.fromFuture(result);
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


        if (productCacheService.isProductSearchResultByUrl(productUrl)) {
            var cache = productCacheService.retrieveProductSearchResultByUrl(productUrl);
            return Mono.just(new SearchProductDto(localeId, composedCatalogKey, cache));
        }

        var result = searchItemByProductUrlLogic(filterProductByUrl)
                .thenApply(value -> saveProductToDatabase(value, null, composedCatalogKey,
                        filterProductByUrl.getStoreId()))
                .exceptionally(t -> {
                    log.error(t.getMessage());
                    return SearchProductDto.builder().build();
                });

        return Mono.fromFuture(result);
    }

    @Override
    public Mono<ProductListItemDto> updateProductListItem(ProductListItemDto productListItem) {
        if (isLocaleOrCatalogOrStoreDisabled(null)) {
            return Mono.just(productListItem);
        }

        var result = updateItemLogic(productListItem)
                .exceptionally(t -> {
                    log.error(t.getMessage());
                    return productListItem;
                });

        return Mono.fromFuture(result);
    }

    protected Map<String, Object> generateCatalogData(String storeId) {
        var displayOptions = new HashMap<String, Object>();

        optionalCatalog.ifPresent(value -> displayOptions.put("catalogName", value.getName()));
        findStore(storeId).ifPresent(value -> displayOptions.put("storeName", value.getName()));
        displayOptions.put("historyEnabled", isLocaleOrCatalogOrStoreHistoryEnabled(storeId));

        return displayOptions;
    }

    private SearchProductDto saveProductToDatabase(SearchProductDto searchProductDto, String query,
                                                   String composedCatalogKey, String storeId) {
        if (isHistoryEnabled && isLocaleOrCatalogOrStoreHistoryEnabled(storeId)) {
            var searchResultDto = new SearchProductsDto(localeId, composedCatalogKey,
                    List.of(searchProductDto.getProduct()), generateCatalogData(storeId));
            CompletableFuture.runAsync(() -> productDatabaseService.saveSearchResult(searchResultDto, query));
        }

        return searchProductDto;
    }

    private SearchProductsDto saveProductsToDatabaseAndCache(SearchProductsDto searchProductsDto, String query,
                                                             String composedCatalogKey, String storeId) {
        if (isHistoryEnabled && isLocaleOrCatalogOrStoreHistoryEnabled(storeId)) {
            searchProductsDto.getProducts().stream()
                    .map(product -> new SearchProductDto(searchProductsDto.getLocale(), searchProductsDto.getCatalog(),
                            product))
                    .forEach(value -> saveProductToDatabase(value, query, composedCatalogKey, storeId));
        }

        if (isCacheEnabled && isLocaleOrCatalogOrStoreCacheEnabled(storeId)) {
            CompletableFuture.runAsync(() -> productCacheService.cacheProductSearchResult(localeId, composedCatalogKey,
                    query, searchProductsDto.getProducts()));
        }

        return searchProductsDto;
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
