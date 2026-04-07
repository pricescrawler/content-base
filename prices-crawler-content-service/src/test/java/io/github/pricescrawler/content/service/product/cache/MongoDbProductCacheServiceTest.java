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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

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

        when(productCacheDataRepository.findById(anyString())).thenReturn(Mono.empty());
        when(productCacheDataRepository.save(any(ProductCacheDao.class))).thenReturn(Mono.just(new ProductCacheDao()));

        productCacheService.cacheProductSearchResult(locale, catalog, reference, products).block();
        verify(productCacheDataRepository, times(1)).save(any(ProductCacheDao.class));
    }

    @Test
    void isProductSearchResultByUrl() {
        var url = "http://test-url";
        when(productCacheDataRepository.findAll()).thenReturn(Flux.empty());
        assertFalse(productCacheService.isProductSearchResultByUrl(url).block());
    }

    @Test
    void retrieveProductSearchResult() {
        var locale = "local";
        var catalog = "demo";
        var reference = "123";
        var key = "local.demo.123";

        when(productCacheDataRepository.findById(key)).thenReturn(Mono.just(new ProductCacheDao()));

        var retrievedProducts = productCacheService.retrieveProductSearchResult(locale, catalog, reference).block();
        assertNotNull(retrievedProducts);
        assertTrue(retrievedProducts.isEmpty());
    }

    @Test
    void retrieveProductSearchResultByUrl() {
        var url = "http://test-url";
        when(productCacheDataRepository.findAll()).thenReturn(Flux.empty());
        assertNotNull(productCacheService.retrieveProductSearchResultByUrl(url).block());
    }

    @Test
    void deleteOutdatedProductSearchResults() {
        when(productCacheDataRepository.findAll()).thenReturn(Flux.empty());
        assertDoesNotThrow(() -> productCacheService.deleteOutdatedProductSearchResults().block());
    }
}
