package io.github.pricescrawler.content.controller.catalog;

import io.github.pricescrawler.content.common.dto.catalog.LocaleDto;
import io.github.pricescrawler.content.service.catalog.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/locales")
@ConditionalOnProperty("prices.crawler.controller.catalog.enabled")
public class CatalogController {
    private final CatalogService catalogService;

    @GetMapping
    public Flux<LocaleDto> getLocales() {
        return catalogService.searchLocales();
    }

    @GetMapping("/{locale}")
    public Mono<LocaleDto> getLocale(@PathVariable String locale) {
        return catalogService.searchLocaleById(locale)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s locale not found", locale))));
    }
}
