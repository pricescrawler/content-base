package io.github.pricescrawler.content.service.product.base;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import io.github.pricescrawler.content.common.dao.catalog.LocaleDao;
import io.github.pricescrawler.content.common.dao.catalog.StoreDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByQueryDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByUrlDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductsDto;
import io.github.pricescrawler.content.repository.catalog.CatalogDataService;
import io.github.pricescrawler.content.repository.product.ProductDataService;
import io.github.pricescrawler.content.repository.product.history.ProductHistoryDataService;
import io.github.pricescrawler.content.service.product.cache.ProductCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BaseProductServiceRefreshTest {
    @Mock
    private CatalogDataService catalogDataService;

    @Mock
    private ProductDataService productDataService;

    @Mock
    private ProductCacheService productCacheService;

    @Mock
    private ProductHistoryDataService productHistoryDataService;

    private BaseProductService productService;

    @BeforeEach
    void setUp() {
        when(catalogDataService.findLocaleById(anyString())).thenReturn(Mono.empty());
        when(catalogDataService.findCatalogByIdAndLocaleId(anyString(), anyString())).thenReturn(Mono.empty());
        when(productCacheService.isProductSearchResultCached(any(), any(), any())).thenReturn(Mono.just(false));
        when(productCacheService.isProductSearchResultByUrl(any())).thenReturn(Mono.just(false));

        productService = new BaseProductService("local", "demo",
                catalogDataService, productDataService, productCacheService, productHistoryDataService) {
            @Override
            public List<ProductDto> parseProductsFromContent(String catalogKey, String content, String dateTime) {
                return List.of();
            }

            @Override
            public ProductDto parseProductFromContent(String catalogKey, String query, String content, String dateTime) {
                return ProductDto.builder().build();
            }

            @Override
            protected Mono<SearchProductsDto> searchItemLogic(FilterProductByQueryDto filterProduct) {
                return Mono.just(new SearchProductsDto());
            }

            @Override
            protected Mono<SearchProductDto> searchItemByProductUrlLogic(FilterProductByUrlDto filterProductByUrl) {
                return Mono.just(new SearchProductDto());
            }

            @Override
            protected Mono<ProductListItemDto> updateItemLogic(ProductListItemDto productListItem) {
                return Mono.just(productListItem);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private Optional<CatalogDao> currentCatalog() {
        return (Optional<CatalogDao>) ReflectionTestUtils.getField(productService, "optionalCatalog");
    }

    private void makeStale() {
        ReflectionTestUtils.setField(productService, "catalogDataRefreshSeconds", 60L);
        ReflectionTestUtils.setField(productService, "lastCatalogDataRefreshMillis", 0L);
    }

    private FilterProductByQueryDto queryFilter() {
        return FilterProductByQueryDto.builder().composedCatalogKey("local.demo").query("dummy").build();
    }

    @Test
    void staleCatalogDataIsRefreshedOnSearch() {
        var refreshedCatalog = CatalogDao.builder().id("demo").stores(List.of()).build();
        var refreshedLocale = LocaleDao.builder().id("local").build();

        when(catalogDataService.findCatalogByIdAndLocaleId(anyString(), anyString()))
                .thenReturn(Mono.just(refreshedCatalog));
        when(catalogDataService.findLocaleById(anyString())).thenReturn(Mono.just(refreshedLocale));
        makeStale();

        productService.searchProductByQuery(queryFilter()).block();

        assertTrue(currentCatalog().isPresent(), "Catalog snapshot should be refreshed from the database");
        assertEquals("demo", currentCatalog().get().getId());
    }

    @Test
    void refreshDisabledKeepsStartupSnapshot() {
        when(catalogDataService.findCatalogByIdAndLocaleId(anyString(), anyString()))
                .thenReturn(Mono.just(CatalogDao.builder().stores(List.of()).build()));
        ReflectionTestUtils.setField(productService, "catalogDataRefreshSeconds", 0L);
        ReflectionTestUtils.setField(productService, "lastCatalogDataRefreshMillis", 0L);

        productService.searchProductByQuery(queryFilter()).block();

        assertTrue(currentCatalog().isEmpty(), "Snapshot must not change when refresh is disabled");
    }

    @Test
    void freshSnapshotIsNotRefreshed() {
        when(catalogDataService.findCatalogByIdAndLocaleId(anyString(), anyString()))
                .thenReturn(Mono.just(CatalogDao.builder().stores(List.of()).build()));
        ReflectionTestUtils.setField(productService, "catalogDataRefreshSeconds", 3600L);

        productService.searchProductByQuery(queryFilter()).block();

        assertTrue(currentCatalog().isEmpty(), "Snapshot within TTL must not be re-read");
    }

    @Test
    void refreshErrorKeepsPreviousSnapshotAndDoesNotFailRequest() {
        when(catalogDataService.findCatalogByIdAndLocaleId(anyString(), anyString()))
                .thenReturn(Mono.error(new IllegalStateException("db down")));
        when(catalogDataService.findLocaleById(anyString()))
                .thenReturn(Mono.error(new IllegalStateException("db down")));
        makeStale();

        assertDoesNotThrow(() -> productService.searchProductByQuery(queryFilter()).block());
        assertTrue(currentCatalog().isEmpty(), "Previous snapshot should be kept on refresh failure");
    }

    @Test
    void deactivatedCatalogTakesEffectAfterRefresh() {
        var inactiveCatalog = CatalogDao.builder().id("demo").isActive(false).stores(List.of()).build();
        var activeLocale = LocaleDao.builder().id("local").isActive(true).build();

        when(catalogDataService.findCatalogByIdAndLocaleId(anyString(), anyString()))
                .thenReturn(Mono.just(inactiveCatalog));
        when(catalogDataService.findLocaleById(anyString())).thenReturn(Mono.just(activeLocale));
        makeStale();

        // First call triggers the refresh; second call must observe the deactivated catalog.
        productService.searchProductByQuery(queryFilter()).block();
        var result = productService.searchProductByQuery(queryFilter()).block();

        assertNotNull(result);
        assertTrue(result.getProducts().isEmpty(), "Deactivated catalog should return no products");
    }

    private void useCatalogWithStore(boolean storeActive) {
        var store = StoreDao.builder().id("store1").name("Store 1").isActive(storeActive).build();
        ReflectionTestUtils.setField(productService, "optionalLocale",
                Optional.of(LocaleDao.builder().id("local").isActive(true).build()));
        ReflectionTestUtils.setField(productService, "optionalCatalog",
                Optional.of(CatalogDao.builder().id("demo").stores(List.of(store)).build()));
    }

    private FilterProductByQueryDto storeQueryFilter() {
        return FilterProductByQueryDto.builder().composedCatalogKey("demo#store1").storeId("store1")
                .query("dummy").build();
    }

    @Test
    void deactivatedStoreIsNotSearched() {
        useCatalogWithStore(false);

        var result = productService.searchProductByQuery(storeQueryFilter()).block();

        assertNotNull(result);
        assertTrue(result.getProducts().isEmpty(), "Deactivated store should return no products");
        verify(productCacheService, never()).isProductSearchResultCached(any(), any(), any());
    }

    @Test
    void activeStoreIsSearched() {
        useCatalogWithStore(true);

        productService.searchProductByQuery(storeQueryFilter()).block();

        verify(productCacheService).isProductSearchResultCached("local", "demo#store1", "dummy");
    }

    @Test
    void catalogWithoutStoresDoesNotFail() {
        ReflectionTestUtils.setField(productService, "optionalCatalog",
                Optional.of(CatalogDao.builder().id("demo").build()));

        assertDoesNotThrow(() -> productService.searchProductByQuery(storeQueryFilter()).block());
    }
}
