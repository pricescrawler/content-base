package io.github.pricescrawler.content.repository.catalog;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import io.github.pricescrawler.content.common.dao.catalog.LocaleDao;

import reactor.core.publisher.Mono;

public interface CatalogDataService {
    /**
     * Finds a locale with the specified ID.
     *
     * @param id the ID of the locale to find
     * @return a Mono containing the {@link LocaleDao} if found, or an empty Mono if not found
     */
    Mono<LocaleDao> findLocaleById(String id);

    /**
     * Finds a catalog with the specified ID and locale ID.
     *
     * @param id       the ID of the catalog to find
     * @param localeId the ID of the locale for the catalog
     * @return a Mono containing the {@link CatalogDao} if found, or an empty Mono if not found
     */
    Mono<CatalogDao> findCatalogByIdAndLocaleId(String id, String localeId);
}
