package io.github.scafer.prices.crawler.content.repository.product;


import io.github.scafer.prices.crawler.content.common.dao.product.ProductDao;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductsDto;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ProductDataService {
    Optional<ProductDao> findProduct(String locale, String catalog, String reference);

    CompletableFuture<Void> saveSearchResult(SearchProductsDto searchProductsDto, String query);
}
