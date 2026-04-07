package io.github.pricescrawler.content.repository.product.incident;

import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import reactor.core.publisher.Mono;

public interface ProductIncidentDataService {
    /**
     * Saves an incident involving the specified product to the database.
     *
     * @param product     the product involved in the incident
     * @param lastProduct the latest version of the product
     * @param query       the search query used to find the product
     * @return a Mono that completes when the incident is saved
     */
    Mono<Void> saveIncident(ProductHistoryDao product, ProductDto lastProduct, String query);

    /**
     * Closes an incident with the specified key.
     *
     * @param key   the key of the incident to close
     * @param merge whether to merge the incident with any related incidents
     * @return a Mono containing true if the incident was closed, false otherwise
     */
    Mono<Boolean> closeIncident(String key, boolean merge);
}
