package io.github.pricescrawler.content.service.product.cache;

import io.github.pricescrawler.content.common.dto.catalog.LocaleDto;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.service.catalog.CatalogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        when(catalogService.searchLocaleById(anyString())).thenReturn(Optional.of(LocaleDto.builder().build()));
        productCacheService.cacheProductSearchResult(locale, catalog, reference, products);

        assertTrue(productCacheService.isProductSearchResultCached(locale, catalog, reference));
        assertTrue(productCacheService.retrieveProductSearchResult(locale, catalog, reference).isEmpty());
    }

    @Test
    void isProductSearchResultCached_notCached() {
        var locale = "local";
        var catalog = "demo";
        var reference = "dummy";

        assertFalse(productCacheService.isProductSearchResultCached(locale, catalog, reference));
        assertTrue(productCacheService.retrieveProductSearchResult(locale, catalog, reference).isEmpty());
    }

    @Test
    void isProductSearchResultByUrl_notCached() {
        var url = "https://demo.local";

        assertFalse(productCacheService.isProductSearchResultByUrl(url));
        assertNull(productCacheService.retrieveProductSearchResultByUrl(url).getId());
    }

    @Test
    void retrieveProductSearchResult_success() {
        var locale = "local";
        var catalog = "demo";
        var reference = "2";
        var products = List.of(new ProductDto());

        productCacheService.cacheProductSearchResult(locale, catalog, reference, products);

        var retrievedProducts = productCacheService.retrieveProductSearchResult(locale, catalog, reference);
        assertEquals(products.size(), retrievedProducts.size());
    }
}
