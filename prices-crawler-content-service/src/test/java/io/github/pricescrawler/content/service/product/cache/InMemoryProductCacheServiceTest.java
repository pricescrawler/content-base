package io.github.pricescrawler.content.service.product.cache;

import io.github.pricescrawler.content.common.dto.catalog.LocaleDto;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.service.catalog.CatalogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryProductCacheServiceTest {
    @Mock
    private CatalogService catalogService;

    @InjectMocks
    private InMemoryProductCacheService productCacheService;

    @Test
    void cacheProductSearchResult() {
        var locale = "local";
        var catalog = "demo";
        var reference = "1";
        var products = new ArrayList<ProductDto>();

        when(catalogService.searchLocaleById(anyString())).thenReturn(Mono.just(LocaleDto.builder().build()));
        productCacheService.cacheProductSearchResult(locale, catalog, reference, products).block();

        assertTrue(productCacheService.isProductSearchResultCached(locale, catalog, reference).block());
        assertTrue(productCacheService.retrieveProductSearchResult(locale, catalog, reference).block().isEmpty());
    }

    @Test
    void isProductSearchResultCached_notCached() {
        var locale = "local";
        var catalog = "demo";
        var reference = "dummy";

        assertFalse(productCacheService.isProductSearchResultCached(locale, catalog, reference).block());
        assertTrue(productCacheService.retrieveProductSearchResult(locale, catalog, reference).block().isEmpty());
    }

    @Test
    void isProductSearchResultByUrl_notCached() {
        var url = "https://demo.local";

        assertFalse(productCacheService.isProductSearchResultByUrl(url).block());
        assertNull(productCacheService.retrieveProductSearchResultByUrl(url).block().getId());
    }

    @Test
    void retrieveProductSearchResult_success() {
        var locale = "local";
        var catalog = "demo";
        var reference = "2";
        var products = List.of(new ProductDto());

        productCacheService.cacheProductSearchResult(locale, catalog, reference, products).block();

        var retrievedProducts = productCacheService.retrieveProductSearchResult(locale, catalog, reference).block();
        assertEquals(products.size(), retrievedProducts.size());
    }
}
