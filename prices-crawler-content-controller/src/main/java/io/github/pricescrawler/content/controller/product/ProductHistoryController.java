package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dao.catalog.LocaleDao;
import io.github.pricescrawler.content.common.dto.product.ProductHistoryDto;
import io.github.pricescrawler.content.repository.catalog.CatalogDataService;
import io.github.pricescrawler.content.repository.product.history.ProductHistoryDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/history")
@ConditionalOnProperty("prices.crawler.controller.product.history.enabled")
public class ProductHistoryController {
    private final CatalogDataService catalogDataService;
    private final ProductHistoryDataService productHistoryDataService;

    @GetMapping
    public List<ProductHistoryDto> searchProductByEanUpc(@RequestParam String eanUpc) {
        return productHistoryDataService.findProductsByEanUpc(eanUpc)
                .stream()
                .map(value -> {
                    var timezone = catalogDataService.findLocaleById(value.getLocale()).map(LocaleDao::getTimezone).orElse(null);
                    return new ProductHistoryDto(value, timezone);
                })
                .toList();
    }

    @GetMapping("/{locale}/{catalog}/{reference}")
    public ProductHistoryDto productHistory(@PathVariable String locale, @PathVariable String catalog, @PathVariable String reference,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        var timezone = catalogDataService.findLocaleById(locale).map(LocaleDao::getTimezone).orElse(null);
        return productHistoryDataService.findProduct(locale, catalog, reference)
                .map(value -> new ProductHistoryDto(value, timezone).withPrices(startDate, endDate))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s product reference not found", reference)));
    }
}