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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseProductServiceTest {
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

    @Test
    void searchProductByQuery_localeDisabled() {
        var filterProductByQueryDto = new FilterProductByQueryDto();
        var result = productService.searchProductByQuery(filterProductByQueryDto);

        assertNotNull(result.share().block());
    }

    @Test
    void searchProductByQuery_cacheEnabled() {
        when(productCacheService.isProductSearchResultCached(anyString(), anyString(), anyString())).thenReturn(true);

        var filterProductByQueryDto = FilterProductByQueryDto.builder().composedCatalogKey("local.demo.1").query("dummy").build();
        var result = productService.searchProductByQuery(filterProductByQueryDto);

        assertNotNull(result.share().block());
        assertTrue(result.share().block().getProducts().isEmpty());
    }

    @Test
    void searchProductByQuery_historyEnabled() {
        when(productCacheService.isProductSearchResultCached(anyString(), anyString(), anyString())).thenReturn(false);

        var filterProductByQueryDto = FilterProductByQueryDto.builder().composedCatalogKey("local.demo.1").query("dummy").build();
        var result = productService.searchProductByQuery(filterProductByQueryDto);

        assertNotNull(result.share().block());
    }

    @Test
    void searchProductByProductUrl_localeDisabled() {
        var filterProductByUrlDto = FilterProductByUrlDto.builder().composedCatalogKey("local.demo.1").url("https://dummy.local").build();
        var result = productService.searchProductByProductUrl(filterProductByUrlDto);

        assertNotNull(result.share().block());
        assertNull(result.share().block().getProduct());
    }

    @Test
    void searchProductByProductUrl_cacheEnabled() {
        when(productCacheService.isProductSearchResultByUrl(anyString())).thenReturn(true);

        var filterProductByUrlDto = FilterProductByUrlDto.builder().composedCatalogKey("local.demo.1").url("https://dummy.local").build();
        var result = productService.searchProductByProductUrl(filterProductByUrlDto);

        assertNotNull(result.share().block());
        assertNull(result.share().block().getProduct());
    }

    @Test
    void searchProductByProductUrl_historyEnabled() {
        var filterProductByUrlDto = FilterProductByUrlDto.builder().composedCatalogKey("local.demo.1").url("https://dummy.local").build();
        var result = productService.searchProductByProductUrl(filterProductByUrlDto);

        assertNotNull(result.share().block());
    }

    @Test
    void updateProductListItem() {
        var productListItemDto = new ProductListItemDto();
        var result = productService.updateProductListItem(productListItemDto);

        assertNotNull(result.share().block());
        assertEquals(productListItemDto, result.share().block());
    }
}
