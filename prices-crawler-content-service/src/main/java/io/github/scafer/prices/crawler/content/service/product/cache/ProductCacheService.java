package io.github.scafer.prices.crawler.content.service.product.cache;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;

import java.util.List;

public interface ProductCacheService {
    void storeProductsList(String locale, String catalog, String reference, List<ProductDto> products);

    boolean isProductListCached(String locale, String catalog, String reference);

    List<ProductDto> retrieveProductsList(String locale, String catalog, String reference);
}
