package io.github.scafer.prices.crawler.content.service.catalog;

import io.github.scafer.prices.crawler.content.common.dto.catalog.LocaleDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CatalogService {
    List<LocaleDto> searchLocales();

    Optional<LocaleDto> searchLocaleById(String locale);
}
