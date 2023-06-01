package io.github.pricescrawler.content.service.scheduled;

import io.github.pricescrawler.content.service.product.cache.ProductCacheService;
import io.github.pricescrawler.content.service.product.list.ProductListService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BackgroundServiceTest {

    @Test
    void startBackgroundService_ShouldDeleteOutdatedProductListsAndSearchResults() {
        var productListService = mock(ProductListService.class);
        var localProductCacheService = mock(ProductCacheService.class);
        var mongoDbProductCacheService = mock(ProductCacheService.class);

        var backgroundService = new BackgroundService(
                productListService, localProductCacheService, mongoDbProductCacheService);

        backgroundService.startBackgroundService();

        verify(productListService).deleteOutdatedProductLists();
        verify(localProductCacheService).deleteOutdatedProductSearchResults();
        verify(mongoDbProductCacheService).deleteOutdatedProductSearchResults();
    }
}

