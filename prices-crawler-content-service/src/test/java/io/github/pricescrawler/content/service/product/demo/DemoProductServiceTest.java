package io.github.pricescrawler.content.service.product.demo;

import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByQueryDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByUrlDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
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
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DemoProductServiceTest {
    @Mock
    private CatalogDataService catalogDataService;

    @Mock
    private ProductDataService productDataService;

    @Mock
    private ProductCacheService productCacheService;

    @Mock
    private ProductHistoryDataService productHistoryDataService;

    private DemoProductService demoProductService;

    @BeforeEach
    void setUp() {
        when(catalogDataService.findLocaleById(anyString())).thenReturn(Mono.empty());
        when(catalogDataService.findCatalogByIdAndLocaleId(anyString(), anyString())).thenReturn(Mono.empty());
        when(productCacheService.isProductSearchResultCached(any(), any(), any())).thenReturn(Mono.just(false));
        when(productCacheService.isProductSearchResultByUrl(any())).thenReturn(Mono.just(false));

        demoProductService = new DemoProductService(catalogDataService, productDataService,
                productCacheService, productHistoryDataService);
    }

    @Test
    void searchItemLogic() {
        var filterProductByQueryDto = FilterProductByQueryDto.builder().composedCatalogKey("test_catalog").build();
        var searchProductsDtoMono = demoProductService.searchItemLogic(filterProductByQueryDto);

        assertNotNull(searchProductsDtoMono);
    }

    @Test
    void searchItemByProductUrlLogic() {
        var filterProductByUrlDto = FilterProductByUrlDto.builder().composedCatalogKey("test_catalog")
                .url("http://test-url").build();
        var searchProductDtoMono = demoProductService.searchItemByProductUrlLogic(filterProductByUrlDto);

        assertNotNull(searchProductDtoMono);
    }

    @Test
    void updateItemLogic() {
        var productListItemDto = new ProductListItemDto();
        var productListItemDtoMono = demoProductService.updateItemLogic(productListItemDto);

        assertNotNull(productListItemDtoMono);
    }

    @Test
    void parseProductsFromContent() {
        var productDtoList = demoProductService.parseProductsFromContent("test_catalog", null, DateTimeUtils.getCurrentDateTime());

        assertNotNull(productDtoList);
        assertEquals(1, productDtoList.size());
    }

    @Test
    void parseProductFromContent() {
        var productDto = demoProductService.parseProductFromContent("test_catalog", null, null, DateTimeUtils.getCurrentDateTime());

        assertNotNull(productDto);
        assertEquals("Demo Product 1", productDto.getName());
    }
}
