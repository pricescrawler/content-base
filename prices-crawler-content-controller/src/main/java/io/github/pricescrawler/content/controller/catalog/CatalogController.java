package io.github.pricescrawler.content.controller.catalog;

import io.github.pricescrawler.content.common.dto.catalog.LocaleDto;
import io.github.pricescrawler.content.service.catalog.CatalogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/locales")
@ConditionalOnProperty("prices.crawler.controller.catalog.enabled")
public class CatalogController {
    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public List<LocaleDto> getLocales() {
        return catalogService.searchLocales();
    }

    @GetMapping("/{locale}")
    public LocaleDto getLocale(@PathVariable String locale) {
        return catalogService.searchLocaleById(locale)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s locale not found", locale)));
    }
}
