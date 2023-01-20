package io.github.scafer.prices.crawler.content.repository.product.list;

import io.github.scafer.prices.crawler.content.common.dao.product.list.ProductListDao;

import java.util.List;
import java.util.Optional;

public interface ProductListDataService {
    List<ProductListDao> findAllProductList();

    Optional<ProductListDao> findProductListById(String id);

    ProductListDao saveProductList(ProductListDao productList);

    void deleteProductList(String id);
}
