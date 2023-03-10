package io.github.pricescrawler.content.service.product.cache;

import io.github.pricescrawler.content.common.dao.product.cache.ProductCacheDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.cache.ProductCacheDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.common.util.IdUtils;
import io.github.pricescrawler.content.repository.product.cache.ProductCacheDataRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class MongoDbProductCacheService implements ProductCacheService {
    private static final String PRODUCTS_CACHE_REMOVING = "Products Cache: removing {}";

    private final ProductCacheDataRepository productCacheDataRepository;

    public MongoDbProductCacheService(ProductCacheDataRepository productCacheDataRepository) {
        this.productCacheDataRepository = productCacheDataRepository;
    }

    @Override
    public void cacheProductSearchResult(String locale, String catalog, String reference, List<ProductDto> products) {
        var key = IdUtils.parse(locale, catalog, reference);

        if (!productCacheDataRepository.existsById(key)) {
            productCacheDataRepository.save(new ProductCacheDao(key, DateTimeUtils.getCurrentDateTime(), products));
        }
    }

    @Override
    public boolean isProductSearchResultCached(String locale, String catalog, String reference) {
        var isCached = false;
        var key = IdUtils.parse(locale, catalog, reference);

        if (productCacheDataRepository.existsById(key)) {
            var productCacheDao = productCacheDataRepository.findById(key);

            if (productCacheDao.isPresent()) {
                try {
                    var products = productCacheDao.get();

                    if (DateTimeUtils.areDatesOnSameDay(DateTimeUtils.getCurrentDateTime(), products.getDate())) {
                        isCached = true;
                    } else {
                        productCacheDataRepository.deleteById(key);
                        log.info(PRODUCTS_CACHE_REMOVING, key);
                    }
                } catch (Exception ex) {
                    log.error("Products Cache: error - {}", ex.getMessage());
                }
            }
        }

        return isCached;
    }

    @Override
    public boolean isProductSearchResultByUrl(String url) {
        var isCached = false;

        for (var element : productCacheDataRepository.findAll()) {
            for (var product : element.getProducts()) {
                if (product.getProductUrl().equals(url)) {
                    if (DateTimeUtils.areDatesOnSameDay(DateTimeUtils.getCurrentDateTime(), product.getDate())) {
                        return true;
                    } else {
                        log.info(PRODUCTS_CACHE_REMOVING, url);
                        productCacheDataRepository.deleteById(element.getId());
                    }
                }
            }
        }

        return isCached;
    }

    @Override
    public List<ProductDto> retrieveProductSearchResult(String locale, String catalog, String reference) {
        var key = IdUtils.parse(locale, catalog, reference);

        var products = productCacheDataRepository.findById(key)
                .map(ProductCacheDto::getProducts)
                .orElse(new ArrayList<>());

        if (products.isEmpty()) {
            log.info("Products Cache: returning {} - empty", key);
        } else {
            log.info("Products Cache: returning {}", key);
        }

        return products;
    }

    @Override
    public ProductDto retrieveProductSearchResultByUrl(String url) {
        for (var element : productCacheDataRepository.findAll()) {
            for (var product : element.getProducts()) {
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
        for (var entry : productCacheDataRepository.findAll()) {
            if (!DateTimeUtils.areDatesOnSameDay(DateTimeUtils.getCurrentDateTime(), entry.getDate())) {
                log.info(PRODUCTS_CACHE_REMOVING, entry.getId());
                productCacheDataRepository.deleteById(entry.getId());
            }
        }
    }
}
