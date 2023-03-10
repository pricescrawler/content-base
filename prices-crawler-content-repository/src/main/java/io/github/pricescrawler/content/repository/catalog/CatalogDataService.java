package io.github.pricescrawler.content.repository.catalog;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import io.github.pricescrawler.content.common.dao.catalog.LocaleDao;

import java.util.Optional;

public interface CatalogDataService {
    /**
     * Finds a locale with the specified ID.
     *
     * @param id the ID of the locale to find
     * @return an Optional containing the {@link LocaleDao} if found, or an empty Optional if not found
     */
    Optional<LocaleDao> findLocaleById(String id);

    /**
     * Finds a catalog with the specified ID and locale ID.
     *
     * @param id       the ID of the catalog to find
     * @param localeId the ID of the locale for the catalog
     * @return an Optional containing the {@link CatalogDao} if found, or an empty Optional if not found
     */
    Optional<CatalogDao> findCatalogByIdAndLocaleId(String id, String localeId);
}
