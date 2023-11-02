package io.github.pricescrawler.content.repository.product.incident;

import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;

public interface ProductIncidentDataService {
    /**
     * Saves an incident involving the specified product to the database.
     *
     * @param product     the product involved in the incident
     * @param lastProduct the latest version of the product
     * @param query       the search query used to find the product
     */
    void saveIncident(ProductHistoryDao product, ProductDto lastProduct, String query);

    /**
     * Closes an incident with the specified key.
     *
     * @param key   the key of the incident to close
     * @param merge whether to merge the incident with any related incidents
     * @return true if the incident was closed, false otherwise
     */
    boolean closeIncident(String key, boolean merge);
}
