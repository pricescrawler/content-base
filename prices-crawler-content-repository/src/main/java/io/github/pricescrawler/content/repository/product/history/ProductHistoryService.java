package io.github.pricescrawler.content.repository.product.history;


import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductsDto;

import java.util.List;
import java.util.Optional;

public interface ProductHistoryService {
    /**
     * Finds a product with the specified locale, catalog, and reference.
     *
     * @param locale    the locale of the product to find
     * @param catalog   the catalog of the product to find
     * @param reference the reference of the product to find
     * @return an Optional containing the {@link ProductHistoryDao} if found, or an empty Optional if not
     */
    Optional<ProductHistoryDao> findProduct(String locale, String catalog, String reference);

    /**
     * Finds all products with the specified EAN/UPC code.
     *
     * @param eanUpc the EAN/UPC code of the products to find
     * @return a list of {@link ProductHistoryDao} with the specified EAN/UPC code
     */
    List<ProductHistoryDao> findProductsByEanUpc(String eanUpc);

    /**
     * Saves the results of a product search to the database.
     *
     * @param searchProductsDto the search results to save
     * @param query             the search query used to find the products
     */
    void saveSearchResult(SearchProductsDto searchProductsDto, String query);
}
