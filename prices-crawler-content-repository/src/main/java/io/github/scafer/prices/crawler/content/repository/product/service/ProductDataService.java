package io.github.scafer.prices.crawler.content.repository.product.service;


import io.github.scafer.prices.crawler.content.common.dao.product.ProductDao;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductsDto;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ProductDataService {
    Optional<ProductDao> findProduct(String locale, String catalog, String reference);

    List<ProductDao> findProductsByEanUpc(String eanUpc);

    CompletableFuture<Void> saveSearchResult(SearchProductsDto searchProductsDto, String query);
}
