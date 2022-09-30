package io.github.scafer.prices.crawler.content.controller;

import io.github.scafer.prices.crawler.content.common.dto.catalog.LocaleDto;
import io.github.scafer.prices.crawler.content.service.catalog.CatalogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@ConditionalOnProperty("prices.crawler.catalog.controller.enabled")
public class CatalogController {
    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @CrossOrigin
    @GetMapping("/locales")
    public List<LocaleDto> getLocales() {
        return catalogService.searchLocales();
    }

    @CrossOrigin
    @GetMapping("/locales/{locale}")
    public LocaleDto getLocale(@PathVariable String locale) {
        return catalogService.searchLocaleById(locale)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s locale not found", locale)));
    }
}
