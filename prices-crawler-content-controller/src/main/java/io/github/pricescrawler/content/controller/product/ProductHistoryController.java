package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dto.product.ProductHistoryDto;
import io.github.pricescrawler.content.repository.catalog.CatalogDataService;
import io.github.pricescrawler.content.repository.product.history.ProductHistoryDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/history")
@ConditionalOnProperty("prices.crawler.controller.product.history.enabled")
public class ProductHistoryController {
    private final CatalogDataService catalogDataService;
    private final ProductHistoryDataService productHistoryDataService;

    @GetMapping
    public Flux<ProductHistoryDto> searchProductByEanUpc(@RequestParam String eanUpc) {
        return productHistoryDataService.findProductsByEanUpc(eanUpc)
                .flatMap(value -> catalogDataService.findLocaleById(value.getLocale())
                        .map(locale -> new ProductHistoryDto(value, locale.getTimezone()))
                        .defaultIfEmpty(new ProductHistoryDto(value, null)));
    }

    @GetMapping("/{locale}/{catalog}/{reference}")
    public Mono<ProductHistoryDto> productHistory(@PathVariable String locale, @PathVariable String catalog,
                                                  @PathVariable String reference,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        return productHistoryDataService.findProduct(locale, catalog, reference)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s product reference not found", reference))))
                .flatMap(productData -> catalogDataService.findLocaleById(locale)
                        .map(localeDao -> new ProductHistoryDto(productData, localeDao.getTimezone())
                                .withPrices(startDate, endDate))
                        .defaultIfEmpty(new ProductHistoryDto(productData, null).withPrices(startDate, endDate)));
    }
}
