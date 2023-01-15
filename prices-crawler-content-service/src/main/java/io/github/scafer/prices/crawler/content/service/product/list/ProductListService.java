package io.github.scafer.prices.crawler.content.service.product.list;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductListItemDto;

import java.util.List;

public interface ProductListService {
    List<ProductListItemDto> retrieveProductList(String id);

    String saveProductList(List<ProductListItemDto> productListItems);

    void deleteOutdatedProductLists();
}
