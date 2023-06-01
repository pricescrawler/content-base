package io.github.pricescrawler.content.repository.product.list;

import io.github.pricescrawler.content.common.dao.product.list.ProductListDao;

import java.util.List;
import java.util.Optional;

public interface ProductListDataService {

    /**
     * Retrieves all product lists.
     *
     * @return a list of all product lists
     */
    List<ProductListDao> findAllProductList();

    /**
     * Retrieves a product list by its ID.
     *
     * @param id the ID of the product list to retrieve
     * @return an optional containing the product list, or an empty optional if not found
     */
    Optional<ProductListDao> findProductListById(String id);

    /**
     * Saves a product list.
     *
     * @param productList the product list to save
     * @return the saved product list
     */
    ProductListDao saveProductList(ProductListDao productList);

    /**
     * Deletes a product list by its ID.
     *
     * @param id the ID of the product list to delete
     */
    void deleteProductList(String id);
}
