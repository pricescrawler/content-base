package io.github.scafer.prices.crawler.content.repository.product;


import io.github.scafer.prices.crawler.content.common.dao.product.ProductDao;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductsDto;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ProductDataService {
    /**
     * Finds a product with the specified locale, catalog, and reference.
     *
     * @param locale    the locale of the product to find
     * @param catalog   the catalog of the product to find
     * @param reference the reference of the product to find
     * @return an Optional containing the {@link ProductDao} if found, or an empty Optional if not
     */
    Optional<ProductDao> findProduct(String locale, String catalog, String reference);

    /**
     * Finds all products with the specified EAN/UPC code.
     *
     * @param eanUpc the EAN/UPC code of the products to find
     * @return a list of {@link ProductDao} with the specified EAN/UPC code
     */
    List<ProductDao> findProductsByEanUpc(String eanUpc);

    /**
     * Saves the results of a product search to the database.
     *
     * @param searchProductsDto the search results to save
     * @param query             the search query used to find the products
     * @return a CompletableFuture that completes when the save operation is complete
     */
    CompletableFuture<Void> saveSearchResult(SearchProductsDto searchProductsDto, String query);
}
