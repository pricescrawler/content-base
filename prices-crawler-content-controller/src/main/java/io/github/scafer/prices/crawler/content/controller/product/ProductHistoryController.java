package io.github.scafer.prices.crawler.content.controller.product;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDataDto;
import io.github.scafer.prices.crawler.content.repository.product.service.ProductDataService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@ConditionalOnProperty("prices.crawler.product.controller.enabled")
public class ProductHistoryController {
    private final ProductDataService productDataService;

    public ProductHistoryController(ProductDataService productDatabaseService) {
        this.productDataService = productDatabaseService;
    }

    @CrossOrigin
    @GetMapping("/products/history")
    public List<ProductDataDto> searchProductByEanUpc(@RequestParam String eanUpc) {
        return productDataService.findProductsByEanUpc(eanUpc).stream().map(ProductDataDto::new).toList();
    }

    @CrossOrigin
    @GetMapping("/products/history/{locale}/{catalog}/{reference}")
    public ProductDataDto productHistory(@PathVariable String locale, @PathVariable String catalog, @PathVariable String reference,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        return productDataService.findProduct(locale, catalog, reference)
                .map(value ->
                        new ProductDataDto(value).withPrices(startDate, endDate))
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s product reference not found", reference)));
    }
}