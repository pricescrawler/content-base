package io.github.pricescrawler.content.service.product.cache;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.cache.ProductCacheDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.common.util.IdUtils;
import io.github.pricescrawler.content.service.catalog.CatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Primary
@Service
@RequiredArgsConstructor
public class InMemoryProductCacheService implements ProductCacheService {

    private static final String PRODUCTS_CACHE_REMOVING = "Products Cache: removing {}";
    private static final Map<String, ProductCacheDto> cachedProducts = new ConcurrentHashMap<>();

    private final CatalogService catalogService;

    @Override
    public void cacheProductSearchResult(String locale, String catalog, String reference, List<ProductDto> products) {
        var key = IdUtils.parse(locale, catalog, reference);
        cachedProducts.computeIfAbsent(key, k -> new ProductCacheDto(DateTimeUtils.getCurrentDateTime(), products));
    }

    @Override
    public boolean isProductSearchResultCached(String locale, String catalog, String reference) {
        var isCached = false;
        var key = IdUtils.parse(locale, catalog, reference);

        if (cachedProducts.containsKey(key)) {
            var products = cachedProducts.get(key);
            var timezone = catalogService.searchLocaleById(locale).orElseThrow().getTimezone();

            try {
                if (DateTimeUtils.areDatesOnSameDay(DateTimeUtils.getCurrentDateTime(), products.getDate(), timezone)) {
                    isCached = true;
                } else {
                    cachedProducts.remove(key);
                    log.info(PRODUCTS_CACHE_REMOVING, key);
                }
            } catch (Exception ex) {
                log.error("Products Cache: error - {}", ex.getMessage());
            }
        }

        return isCached;
    }

    @Override
    public boolean isProductSearchResultByUrl(String url) {
        var isCached = false;

        for (var element : cachedProducts.entrySet()) {
            for (var product : element.getValue().getProducts()) {
                if (product.getProductUrl().equals(url)) {
                    var timezone = catalogService.searchLocaleById(IdUtils.extractLocaleFromKey(product.getId())).orElseThrow().getTimezone();

                    if (DateTimeUtils.areDatesOnSameDay(DateTimeUtils.getCurrentDateTime(), product.getDate(), timezone)) {
                        return true;
                    } else {
                        log.info(PRODUCTS_CACHE_REMOVING, url);
                        cachedProducts.remove(element.getKey());
                    }
                }
            }
        }

        return isCached;
    }

    @Override
    public List<ProductDto> retrieveProductSearchResult(String locale, String catalog, String reference) {
        List<ProductDto> products = new ArrayList<>();
        var key = IdUtils.parse(locale, catalog, reference);

        if (cachedProducts.containsKey(key)) {
            products = cachedProducts.get(key).getProducts();

            if (products.isEmpty()) {
                log.info("Products Cache: returning {} - empty", key);
            } else {
                log.info("Products Cache: returning {}", key);
            }
        }

        return products;
    }

    @Override
    public ProductDto retrieveProductSearchResultByUrl(String url) {
        for (var element : cachedProducts.entrySet()) {
            for (var product : element.getValue().getProducts()) {
                if (product.getProductUrl().equals(url)) {
                    log.info("Products Cache: returning {}", url);
                    return product;
                }
            }
        }

        return ProductDto.builder().build();
    }

    @Override
    public void deleteOutdatedProductSearchResults() {
        for (var entry : cachedProducts.entrySet()) {
            var timezone = catalogService.searchLocaleById(entry.getKey()).orElseThrow().getTimezone();

            if (!DateTimeUtils.areDatesOnSameDay(DateTimeUtils.getCurrentDateTime(), entry.getValue().getDate(), timezone)) {
                log.info(PRODUCTS_CACHE_REMOVING, entry.getKey());
                cachedProducts.remove(entry.getKey());
            }
        }
    }
}
