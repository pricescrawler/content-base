package io.github.pricescrawler.content.service.product.base;

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
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BaseProductServiceMetricsTest {
    private static final String METRIC = "prices.crawler.product.search";

    @Mock
    private CatalogDataService catalogDataService;

    @Mock
    private ProductDataService productDataService;

    @Mock
    private ProductCacheService productCacheService;

    @Mock
    private ProductHistoryDataService productHistoryDataService;

    private SimpleMeterRegistry meterRegistry;
    private Mono<SearchProductsDto> searchResult;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        Metrics.addRegistry(meterRegistry);

        when(catalogDataService.findLocaleById(anyString())).thenReturn(Mono.empty());
        when(catalogDataService.findCatalogByIdAndLocaleId(anyString(), anyString())).thenReturn(Mono.empty());
        when(productCacheService.isProductSearchResultCached(any(), any(), any())).thenReturn(Mono.just(false));
        when(productCacheService.isProductSearchResultByUrl(any())).thenReturn(Mono.just(false));
        when(productCacheService.retrieveProductSearchResult(any(), any(), any())).thenReturn(Mono.just(List.of()));
    }

    @AfterEach
    void tearDown() {
        Metrics.removeRegistry(meterRegistry);
        meterRegistry.close();
    }

    private BaseProductService createService() {
        return new BaseProductService("local", "demo",
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
                return searchResult;
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

    private double counterValue(String type, String outcome) {
        var counter = meterRegistry.find(METRIC)
                .tag("locale", "local")
                .tag("catalog", "demo")
                .tag("type", type)
                .tag("outcome", outcome)
                .counter();

        return counter == null ? 0.0 : counter.count();
    }

    private FilterProductByQueryDto queryFilter() {
        return FilterProductByQueryDto.builder().composedCatalogKey("local.demo").query("dummy").build();
    }

    @Test
    void emptySearchResultIncrementsEmptyOutcome() {
        searchResult = Mono.just(new SearchProductsDto());

        createService().searchProductByQuery(queryFilter()).block();

        assertEquals(1.0, counterValue("query", "empty"));
        assertEquals(0.0, counterValue("query", "success"));
    }

    @Test
    void nonEmptySearchResultIncrementsSuccessOutcome() {
        searchResult = Mono.just(new SearchProductsDto("local", "local.demo",
                List.of(ProductDto.builder().build()), null));

        createService().searchProductByQuery(queryFilter()).block();

        assertEquals(1.0, counterValue("query", "success"));
        assertEquals(0.0, counterValue("query", "empty"));
    }

    @Test
    void failedSearchIncrementsErrorOutcome() {
        searchResult = Mono.error(new IllegalStateException("upstream broke"));

        createService().searchProductByQuery(queryFilter()).block();

        assertEquals(1.0, counterValue("query", "error"));
    }

    @Test
    void cachedSearchIncrementsCachedOutcome() {
        searchResult = Mono.just(new SearchProductsDto());
        when(productCacheService.isProductSearchResultCached(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(true));

        createService().searchProductByQuery(queryFilter()).block();

        assertEquals(1.0, counterValue("query", "cached"));
    }
}
