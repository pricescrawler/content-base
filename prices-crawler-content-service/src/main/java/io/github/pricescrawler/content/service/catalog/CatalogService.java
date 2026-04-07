package io.github.pricescrawler.content.service.catalog;

import io.github.pricescrawler.content.common.dto.catalog.LocaleDto;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface CatalogService {
    /**
     * Retrieves a list of all available locales.
     *
     * @return a list of {@link LocaleDto} objects representing the available locales
     */
    Flux<LocaleDto> searchLocales();

    /**
     * Searches a locale with the specified ID.
     *
     * @param id the Locale ID
     * @return an Optional containing the {@link LocaleDto} object if found
     */
    Mono<LocaleDto> searchLocaleById(String id);
}
