package io.github.scafer.prices.crawler.content.repository.product.incident;

import io.github.scafer.prices.crawler.content.common.dao.product.ProductDao;
import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;

import java.util.concurrent.CompletableFuture;

public interface ProductIncidentDataService {
    CompletableFuture<Void> saveIncident(ProductDao product, ProductDto newProduct, String query);

    boolean closeIncident(String key, boolean merge);
}
