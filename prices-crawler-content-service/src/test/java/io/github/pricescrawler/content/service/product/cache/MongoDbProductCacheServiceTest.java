package io.github.pricescrawler.content.service.product.cache;

import io.github.pricescrawler.content.common.dao.product.cache.ProductCacheDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.repository.product.cache.ProductCacheDataRepository;
import io.github.pricescrawler.content.service.catalog.CatalogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoDbProductCacheServiceTest {
    @Mock
    private CatalogService catalogService;

    @Mock
    private ProductCacheDataRepository productCacheDataRepository;

    @InjectMocks
    private MongoDbProductCacheService productCacheService;

    @Test
    void cacheProductSearchResult() {
        var locale = "local";
        var catalog = "test_catalog";
        var reference = "123";
        var products = new ArrayList<ProductDto>();

        when(productCacheDataRepository.existsById(anyString())).thenReturn(false);

        productCacheService.cacheProductSearchResult(locale, catalog, reference, products);
        verify(productCacheDataRepository, times(1)).save(any(ProductCacheDao.class));
    }

    @Test
    void isProductSearchResultByUrl() {
        var url = "http://test-url";
        when(productCacheDataRepository.findAll()).thenReturn(new ArrayList<>());
        assertFalse(productCacheService.isProductSearchResultByUrl(url));
    }

    @Test
    void retrieveProductSearchResult() {
        var locale = "local";
        var catalog = "demo";
        var reference = "123";
        var key = "local.demo.123";

        when(productCacheDataRepository.findById(key)).thenReturn(Optional.of(new ProductCacheDao()));

        var retrievedProducts = productCacheService.retrieveProductSearchResult(locale, catalog, reference);
        assertNotNull(retrievedProducts);
        assertTrue(retrievedProducts.isEmpty());
    }

    @Test
    void retrieveProductSearchResultByUrl() {
        var url = "http://test-url";
        assertNotNull(productCacheService.retrieveProductSearchResultByUrl(url));
    }

    @Test
    void deleteOutdatedProductSearchResults() {
        when(productCacheDataRepository.findAll()).thenReturn(new ArrayList<>());
        assertDoesNotThrow(() -> productCacheService.deleteOutdatedProductSearchResults());
    }
}
