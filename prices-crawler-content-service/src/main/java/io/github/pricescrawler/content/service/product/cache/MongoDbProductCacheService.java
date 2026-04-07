package io.github.pricescrawler.content.service.product.cache;

import io.github.pricescrawler.content.common.dao.product.cache.ProductCacheDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.cache.ProductCacheDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.common.util.IdUtils;
import io.github.pricescrawler.content.repository.product.cache.ProductCacheDataRepository;
import io.github.pricescrawler.content.service.catalog.CatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class MongoDbProductCacheService implements ProductCacheService {
    private static final String PRODUCTS_CACHE_REMOVING = "Products Cache: removing {}";
    private static final String PRODUCTS_CACHE_RETURNING = "Products Cache: returning {}";

    private final CatalogService catalogService;
    private final ProductCacheDataRepository productCacheDataRepository;

    @Override
    public Mono<Void> cacheProductSearchResult(String locale, String catalog, String reference, List<ProductDto> products) {
        var key = IdUtils.parse(locale, catalog, reference);
        return productCacheDataRepository.findById(key)
                .hasElement()
                .flatMap(exists -> exists ? Mono.empty() :
                        productCacheDataRepository.save(new ProductCacheDao(key, DateTimeUtils.getCurrentDateTime(), products)).then());
    }

    @Override
    public Mono<Boolean> isProductSearchResultCached(String locale, String catalog, String reference) {
        var key = IdUtils.parse(locale, catalog, reference);
        return productCacheDataRepository.findById(key)
                .flatMap(productCacheDao -> catalogService.searchLocaleById(locale)
                        .flatMap(localeDto -> {
                            try {
                                if (DateTimeUtils.areDatesOnSameDay(DateTimeUtils.getCurrentDateTime(), productCacheDao.getDate(), localeDto.getTimezone())) {
                                    return Mono.just(true);
                                } else {
                                    log.info(PRODUCTS_CACHE_REMOVING, key);
                                    return productCacheDataRepository.deleteById(key).thenReturn(false);
                                }
                            } catch (Exception ex) {
                                log.error("Products Cache: error - {}", ex.getMessage());
                                return Mono.just(false);
                            }
                        })
                )
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> isProductSearchResultByUrl(String url) {
        return productCacheDataRepository.findAll()
                .flatMap(element -> Flux.fromIterable(element.getProducts())
                        .filter(product -> url.equals(product.getProductUrl()))
                        .flatMap(product -> catalogService.searchLocaleById(IdUtils.extractLocaleFromKey(product.getId()))
                                .flatMap(localeDto -> {
                                    if (DateTimeUtils.areDatesOnSameDay(DateTimeUtils.getCurrentDateTime(), product.getDate(), localeDto.getTimezone())) {
                                        return Mono.just(true);
                                    } else {
                                        log.info(PRODUCTS_CACHE_REMOVING, url);
                                        return productCacheDataRepository.deleteById(element.getId()).thenReturn(false);
                                    }
                                })
                                .defaultIfEmpty(false)
                        )
                )
                .filter(Boolean::booleanValue)
                .hasElements();
    }

    @Override
    public Mono<List<ProductDto>> retrieveProductSearchResult(String locale, String catalog, String reference) {
        var key = IdUtils.parse(locale, catalog, reference);
        return productCacheDataRepository.findById(key)
                .map(dao -> {
                    var products = dao.getProducts() != null ? dao.getProducts() : List.<ProductDto>of();
                    if (products.isEmpty()) {
                        log.info("Products Cache: returning {} - empty", key);
                    } else {
                        log.info(PRODUCTS_CACHE_RETURNING, key);
                    }
                    return products;
                })
                .defaultIfEmpty(new ArrayList<>());
    }

    @Override
    public Mono<ProductDto> retrieveProductSearchResultByUrl(String url) {
        return productCacheDataRepository.findAll()
                .flatMap(element -> Flux.fromIterable(element.getProducts())
                        .filter(product -> url.equals(product.getProductUrl()))
                        .doOnNext(product -> log.info(PRODUCTS_CACHE_RETURNING, url))
                )
                .next()
                .defaultIfEmpty(ProductDto.builder().build());
    }

    @Override
    public Mono<Void> deleteOutdatedProductSearchResults() {
        return productCacheDataRepository.findAll()
                .flatMap(entry -> catalogService.searchLocaleById(IdUtils.extractLocaleFromKey(entry.getId()))
                        .flatMap(localeDto -> {
                            if (!DateTimeUtils.areDatesOnSameDay(DateTimeUtils.getCurrentDateTime(), entry.getDate(), localeDto.getTimezone())) {
                                log.info(PRODUCTS_CACHE_REMOVING, entry.getId());
                                return productCacheDataRepository.deleteById(entry.getId());
                            }
                            return Mono.empty();
                        })
                        .onErrorResume(ex -> {
                            log.error("Products Cache: product - {} | error - {}", entry.getId(), ex.getMessage());
                            return Mono.empty();
                        })
                )
                .then();
    }
}
