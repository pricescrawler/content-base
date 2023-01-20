package io.github.scafer.prices.crawler.content.service.product.list;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductListItemDto;

import java.util.List;

public interface ProductListService {
    /**
     * Retrieves the product list by Id from the database.
     *
     * @param id the id of the product list to retrieve.
     * @return a list of {@link ProductListItemDto} objects representing the items in the product list.
     */
    List<ProductListItemDto> retrieveProductList(String id);

    /**
     * Saves a product list to the database.
     *
     * @param productListItems a list of {@link ProductListItemDto} objects representing the items in the product list.
     * @return the id of the saved product list.
     */
    String saveProductList(List<ProductListItemDto> productListItems);

    /**
     * Deletes any product lists that have exceeded their expiration time.
     */
    void deleteOutdatedProductLists();
}
