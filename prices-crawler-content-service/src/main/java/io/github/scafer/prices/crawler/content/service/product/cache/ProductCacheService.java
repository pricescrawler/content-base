package io.github.scafer.prices.crawler.content.service.product.cache;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;

import java.util.List;

public interface ProductCacheService {

    /**
     * Stores in cache a list of products for the specified locale, catalog, and reference.
     *
     * @param locale    the locale for which to store the products
     * @param catalog   the catalog for which to store the products
     * @param reference the reference for which to store the products
     * @param products  the list of products to store
     */
    void cacheProductSearchResult(String locale, String catalog, String reference, List<ProductDto> products);

    /**
     * Determines whether a list of products is cached for the specified locale, catalog, and reference.
     *
     * @param locale    the locale for which to check the cache
     * @param catalog   the catalog for which to check the cache
     * @param reference the reference for which to check the cache
     * @return true if a list of products is cached for the specified locale, catalog, and reference, false otherwise
     */
    boolean isProductSearchResultCached(String locale, String catalog, String reference);

    /**
     * Determines whether a product is cached by the specified URL.
     *
     * @param url the URL of the product to check
     * @return true if the product is cached by the specified URL, false otherwise
     */
    boolean isProductSearchResultByUrl(String url);

    /**
     * Retrieves a list of products from the cache for the specified locale, catalog, and reference.
     *
     * @param locale    the locale for which to retrieve the products
     * @param catalog   the catalog for which to retrieve the products
     * @param reference the reference for which to retrieve the products
     * @return a list of {@link ProductDto} for the specified locale, catalog, and reference
     */
    List<ProductDto> retrieveProductSearchResult(String locale, String catalog, String reference);

    /**
     * Retrieves a product from the cache by the specified URL.
     *
     * @param url the URL of the product to retrieve
     * @return the {@link ProductDto} with the specified URL, or null if no such product is found in the cache
     */
    ProductDto retrieveProductSearchResultByUrl(String url);

    /**
     * Deletes outdated products from the cache.
     * Outdated products are those that have exceeded their expiration time.
     */
    void deleteOutdatedProductSearchResults();
}
