package io.github.pricescrawler.content.repository.product.list;

import io.github.pricescrawler.content.common.dao.product.list.ProductListDao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductListDataService {

    /**
     * Retrieves all product lists.
     *
     * @return a list of all product lists
     */
    Flux<ProductListDao> findAllProductList();

    /**
     * Retrieves a product list by its ID.
     *
     * @param id the ID of the product list to retrieve
     * @return an optional containing the product list, or an empty optional if not found
     */
    Mono<ProductListDao> findProductListById(String id);

    /**
     * Saves a product list.
     *
     * @param productList the product list to save
     * @return the saved product list
     */
    Mono<ProductListDao> saveProductList(ProductListDao productList);

    /**
     * Deletes a product list by its ID.
     *
     * @param id the ID of the product list to delete
     */
    Mono<Void> deleteProductList(String id);
}
