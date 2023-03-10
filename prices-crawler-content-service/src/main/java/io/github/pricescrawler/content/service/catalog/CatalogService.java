package io.github.pricescrawler.content.service.catalog;

import io.github.pricescrawler.content.common.dto.catalog.LocaleDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CatalogService {
    /**
     * Retrieves a list of all available locales.
     *
     * @return a list of {@link LocaleDto} objects representing the available locales
     */
    List<LocaleDto> searchLocales();

    /**
     * Searches a locale with the specified ID.
     *
     * @param id the Locale ID
     * @return an Optional containing the {@link LocaleDto} object if found
     */
    Optional<LocaleDto> searchLocaleById(String id);
}
