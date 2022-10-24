package io.github.scafer.prices.crawler.content.repository.catalog.service;

import io.github.scafer.prices.crawler.content.common.dao.catalog.CatalogDao;
import io.github.scafer.prices.crawler.content.common.dao.catalog.LocaleDao;

import java.util.Optional;

public interface CatalogDataService {
    Optional<LocaleDao> findLocaleById(String locale);

    Optional<CatalogDao> findCatalogByIdAndLocaleId(String catalog, String locale);
}
