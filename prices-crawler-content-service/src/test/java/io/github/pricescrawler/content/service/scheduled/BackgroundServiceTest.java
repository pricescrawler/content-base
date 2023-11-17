package io.github.pricescrawler.content.service.scheduled;

import io.github.pricescrawler.content.service.product.cache.ProductCacheService;
import io.github.pricescrawler.content.service.product.list.ProductListService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BackgroundServiceTest {
    @InjectMocks
    private BackgroundService backgroundService;
    @Mock
    private ProductListService productListService;
    @Mock
    private ProductCacheService inMemoryProductCacheService;
    @Mock
    private ProductCacheService mongoDbProductCacheService;

    @Test
    void startBackgroundService_ShouldDeleteOutdatedProductListsAndSearchResults() {
        var backgroundService = new BackgroundService(
                productListService, inMemoryProductCacheService, mongoDbProductCacheService);

        backgroundService.startBackgroundService();

        verify(productListService).deleteOutdatedProductLists();
        verify(inMemoryProductCacheService).deleteOutdatedProductSearchResults();
        verify(mongoDbProductCacheService).deleteOutdatedProductSearchResults();
    }
}

