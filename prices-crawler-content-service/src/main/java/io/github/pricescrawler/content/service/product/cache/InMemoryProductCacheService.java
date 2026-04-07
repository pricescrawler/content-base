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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    private final Map<String, ProductCacheDto> cachedProducts = new ConcurrentHashMap<>();

    private final CatalogService catalogService;

    @Override
    public Mono<Void> cacheProductSearchResult(String locale, String catalog, String reference, List<ProductDto> products) {
        var key = IdUtils.parse(locale, catalog, reference);
        cachedProducts.computeIfAbsent(key, k -> new ProductCacheDto(DateTimeUtils.getCurrentDateTime(), products));
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> isProductSearchResultCached(String locale, String catalog, String reference) {
        var key = IdUtils.parse(locale, catalog, reference);

        if (!cachedProducts.containsKey(key)) {
            return Mono.just(false);
        }

        var products = cachedProducts.get(key);
        return catalogService.searchLocaleById(locale)
                .map(localeDto -> {
                    try {
                        if (DateTimeUtils.areDatesOnSameDay(DateTimeUtils.getCurrentDateTime(), products.getDate(), localeDto.getTimezone())) {
                            return true;
                        } else {
                            cachedProducts.remove(key);
                            log.info(PRODUCTS_CACHE_REMOVING, key);
                            return false;
                        }
                    } catch (Exception ex) {
                        log.error("Products Cache: error - {}", ex.getMessage());
                        return false;
                    }
                })
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> isProductSearchResultByUrl(String url) {
        for (var element : cachedProducts.entrySet()) {
            for (var product : element.getValue().getProducts()) {
                if (url.equals(product.getProductUrl())) {
                    return catalogService.searchLocaleById(IdUtils.extractLocaleFromKey(product.getId()))
                            .map(localeDto -> {
                                if (DateTimeUtils.areDatesOnSameDay(DateTimeUtils.getCurrentDateTime(), product.getDate(), localeDto.getTimezone())) {
                                    return true;
                                } else {
                                    log.info(PRODUCTS_CACHE_REMOVING, url);
                                    cachedProducts.entrySet().removeIf(e -> e.getKey().equals(element.getKey()));
                                    return false;
                                }
                            })
                            .defaultIfEmpty(false);
                }
            }
        }
        return Mono.just(false);
    }

    @Override
    public Mono<List<ProductDto>> retrieveProductSearchResult(String locale, String catalog, String reference) {
        var key = IdUtils.parse(locale, catalog, reference);
        List<ProductDto> products = cachedProducts.containsKey(key)
                ? new ArrayList<>(cachedProducts.get(key).getProducts())
                : new ArrayList<>();

        if (products.isEmpty()) {
            log.info("Products Cache: returning {} - empty", key);
        } else {
            log.info("Products Cache: returning {}", key);
        }

        return Mono.just(products);
    }

    @Override
    public Mono<ProductDto> retrieveProductSearchResultByUrl(String url) {
        for (var element : cachedProducts.entrySet()) {
            for (var product : element.getValue().getProducts()) {
                if (url.equals(product.getProductUrl())) {
                    log.info("Products Cache: returning {}", url);
                    return Mono.just(product);
                }
            }
        }
        return Mono.just(ProductDto.builder().build());
    }

    @Override
    public Mono<Void> deleteOutdatedProductSearchResults() {
        return Flux.fromIterable(new ArrayList<>(cachedProducts.entrySet()))
                .flatMap(entry -> catalogService.searchLocaleById(IdUtils.extractLocaleFromKey(entry.getKey()))
                        .doOnNext(localeDto -> {
                            if (!DateTimeUtils.areDatesOnSameDay(DateTimeUtils.getCurrentDateTime(), entry.getValue().getDate(), localeDto.getTimezone())) {
                                log.info(PRODUCTS_CACHE_REMOVING, entry.getKey());
                                cachedProducts.remove(entry.getKey());
                            }
                        })
                )
                .then();
    }
}
