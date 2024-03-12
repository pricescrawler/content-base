package io.github.pricescrawler.content.service.product.demo;

import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByQueryDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByUrlDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.repository.catalog.CatalogDataService;
import io.github.pricescrawler.content.repository.product.ProductDataService;
import io.github.pricescrawler.content.repository.product.history.ProductHistoryDataService;
import io.github.pricescrawler.content.service.product.cache.ProductCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DemoProductServiceTest {
    @Mock
    private CatalogDataService catalogDataService;

    @Mock
    private ProductDataService productDataService;

    @Mock
    private ProductCacheService productCacheService;

    @Mock
    private ProductHistoryDataService productHistoryDataService;

    @InjectMocks
    private DemoProductService demoProductService;

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
