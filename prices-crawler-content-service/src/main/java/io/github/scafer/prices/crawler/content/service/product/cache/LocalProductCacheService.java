package io.github.scafer.prices.crawler.content.service.product.cache;

import io.github.scafer.prices.crawler.content.common.dto.cache.ProductCacheDto;
import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
import io.github.scafer.prices.crawler.content.common.util.DateTimeUtils;
import io.github.scafer.prices.crawler.content.common.util.IdUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class LocalProductCacheService implements ProductCacheService {

    private static final Map<String, ProductCacheDto> cachedProducts = new HashMap<>();

    public void storeProductsList(String locale, String catalog, String reference, List<ProductDto> products) {
        var key = IdUtils.parse(locale, catalog, reference);
        cachedProducts.computeIfAbsent(key, k -> new ProductCacheDto(DateTimeUtils.getCurrentDateTime(), products));
    }

    public boolean isProductListCached(String locale, String catalog, String reference) {
        var isCached = false;
        var key = IdUtils.parse(locale, catalog, reference);

        if (cachedProducts.containsKey(key)) {
            var products = cachedProducts.get(key);

            try {
                if (DateTimeUtils.isSameDay(DateTimeUtils.getCurrentDateTime(), products.getDate())) {
                    isCached = true;
                } else {
                    cachedProducts.remove(key);
                    log.info("Products Cache: removing {}", key);
                }
            } catch (Exception ex) {
                log.error("Products Cache: error - {}", ex.getMessage());
            }
        }

        return isCached;
    }

    public List<ProductDto> retrieveProductsList(String locale, String catalog, String reference) {
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
}
