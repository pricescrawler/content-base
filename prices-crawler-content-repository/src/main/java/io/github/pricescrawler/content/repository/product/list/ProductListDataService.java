package io.github.pricescrawler.content.repository.product.list;

import io.github.pricescrawler.content.common.dao.product.list.ProductListDao;

import java.util.List;
import java.util.Optional;

public interface ProductListDataService {
    List<ProductListDao> findAllProductList();

    Optional<ProductListDao> findProductListById(String id);

    ProductListDao saveProductList(ProductListDao productList);

    void deleteProductList(String id);
}
