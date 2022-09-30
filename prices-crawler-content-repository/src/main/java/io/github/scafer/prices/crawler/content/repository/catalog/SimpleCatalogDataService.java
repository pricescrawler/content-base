package io.github.scafer.prices.crawler.content.repository.catalog;

import io.github.scafer.prices.crawler.content.common.dao.catalog.CatalogDao;
import io.github.scafer.prices.crawler.content.common.dao.catalog.LocaleDao;
import io.github.scafer.prices.crawler.content.common.util.IdUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SimpleCatalogDataService implements CatalogDataService {
    private final LocaleDataRepository localeDataRepository;
    private final CatalogDataRepository catalogDataRepository;

    public SimpleCatalogDataService(LocaleDataRepository localeDataRepository, CatalogDataRepository catalogDataRepository) {
        this.localeDataRepository = localeDataRepository;
        this.catalogDataRepository = catalogDataRepository;
    }

    @Override
    public Optional<LocaleDao> findLocaleById(String locale) {
        return localeDataRepository.findById(locale);
    }

    @Override
    public Optional<CatalogDao> findCatalogByIdAndLocaleId(String catalog, String locale) {
        return catalogDataRepository.findById(IdUtils.parse(locale, catalog));
    }
}
