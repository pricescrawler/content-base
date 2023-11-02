package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dto.product.ProductHistoryDto;
import io.github.pricescrawler.content.repository.product.history.ProductHistoryService;
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
    private final ProductHistoryService productHistoryService;

    @GetMapping
    public List<ProductHistoryDto> searchProductByEanUpc(@RequestParam String eanUpc) {
        return productHistoryService.findProductsByEanUpc(eanUpc)
                .stream()
                .map(ProductHistoryDto::new)
                .toList();
    }

    @GetMapping("/{locale}/{catalog}/{reference}")
    public ProductHistoryDto productHistory(@PathVariable String locale, @PathVariable String catalog, @PathVariable String reference,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        return productHistoryService.findProduct(locale, catalog, reference)
                .map(value -> new ProductHistoryDto(value).withPrices(startDate, endDate))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s product reference not found", reference)));
    }
}