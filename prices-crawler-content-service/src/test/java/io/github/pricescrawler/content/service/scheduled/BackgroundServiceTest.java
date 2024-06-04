package io.github.pricescrawler.content.service.scheduled;

import io.github.pricescrawler.content.service.product.cache.ProductCacheService;
import io.github.pricescrawler.content.service.product.list.ProductListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class BackgroundServiceTest {
    @Mock
    private ProductListService productListService;

    @Mock
    private ProductCacheService inMemoryProductCacheService;

    @Mock
    private ProductCacheService mongoDbProductCacheService;

    private BackgroundService backgroundService;

    @BeforeEach
    void setUp() {
        backgroundService = new BackgroundService(productListService, inMemoryProductCacheService, mongoDbProductCacheService);
    }

    @Test
    void startBackgroundService_disabled() throws NoSuchFieldException, IllegalAccessException {
        var enabledField = BackgroundService.class.getDeclaredField("enabled");
        enabledField.setAccessible(true);
        enabledField.set(backgroundService, false);

        backgroundService.startBackgroundService();

        verifyNoInteractions(productListService);
        verifyNoInteractions(inMemoryProductCacheService);
        verifyNoInteractions(mongoDbProductCacheService);
    }

    @Test
    void startBackgroundService_shouldDeleteOutdatedProductListsAndSearchResults() throws NoSuchFieldException, IllegalAccessException {
        var enabledField = BackgroundService.class.getDeclaredField("enabled");
        enabledField.setAccessible(true);
        enabledField.set(backgroundService, true);

        backgroundService.startBackgroundService();

        verify(productListService).deleteOutdatedProductLists();
        verify(inMemoryProductCacheService).deleteOutdatedProductSearchResults();
        verify(mongoDbProductCacheService).deleteOutdatedProductSearchResults();
    }
}
