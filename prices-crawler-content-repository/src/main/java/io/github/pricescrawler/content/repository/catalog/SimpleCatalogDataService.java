package io.github.pricescrawler.content.repository.catalog;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import io.github.pricescrawler.content.common.dao.catalog.LocaleDao;
import io.github.pricescrawler.content.common.util.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SimpleCatalogDataService implements CatalogDataService {
    private final LocaleDataRepository localeDataRepository;
    private final CatalogDataRepository catalogDataRepository;

    @Override
    public Optional<LocaleDao> findLocaleById(String id) {
        return localeDataRepository.findById(id);
    }

    @Override
    public Optional<CatalogDao> findCatalogByIdAndLocaleId(String id, String localeId) {
        return catalogDataRepository.findById(IdUtils.parse(localeId, id));
    }
}
