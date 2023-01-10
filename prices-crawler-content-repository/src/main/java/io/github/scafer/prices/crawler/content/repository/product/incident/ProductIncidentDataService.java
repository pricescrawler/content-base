package io.github.scafer.prices.crawler.content.repository.product.incident;

import io.github.scafer.prices.crawler.content.common.dao.product.ProductDao;
import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;

import java.util.concurrent.CompletableFuture;

public interface ProductIncidentDataService {
    /**
     * Saves an incident involving the specified product to the database.
     *
     * @param product     the product involved in the incident
     * @param lastProduct the latest version of the product
     * @param query       the search query used to find the product
     * @return a CompletableFuture that completes when the save operation is complete
     */
    CompletableFuture<Void> saveIncident(ProductDao product, ProductDto lastProduct, String query);

    /**
     * Closes an incident with the specified key.
     *
     * @param key   the key of the incident to close
     * @param merge whether to merge the incident with any related incidents
     * @return true if the incident was closed, false otherwise
     */
    boolean closeIncident(String key, boolean merge);
}
