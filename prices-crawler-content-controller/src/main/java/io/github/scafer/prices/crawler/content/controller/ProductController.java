package io.github.scafer.prices.crawler.content.controller;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDataDto;
import io.github.scafer.prices.crawler.content.common.dto.product.ProductListItemDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductsDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchQueryDto;
import io.github.scafer.prices.crawler.content.common.util.IdUtils;
import io.github.scafer.prices.crawler.content.repository.product.SimpleProductDataService;
import io.github.scafer.prices.crawler.content.service.product.ProductService;
import io.github.scafer.prices.crawler.content.service.product.provider.ProductServiceProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@ConditionalOnProperty("prices.crawler.product.controller.enabled")
public class ProductController {
    private final SimpleProductDataService productDataService;
    private final ProductServiceProvider productServiceProvider;

    public ProductController(SimpleProductDataService productDatabaseService, ProductServiceProvider productServiceProvider) {
        this.productDataService = productDatabaseService;
        this.productServiceProvider = productServiceProvider;
    }

    @CrossOrigin
    @PostMapping("/products/search")
    public Mono<Collection<SearchProductsDto>> searchProducts(@RequestBody SearchQueryDto searchQuery) {
        var responses = new ArrayList<Mono<SearchProductsDto>>();

        for (String catalog : searchQuery.getCatalogs()) {
            var productService = getProductServiceFromCatalog(catalog);
            responses.add(productService.searchProduct(searchQuery.getQuery()));
        }

        return Mono.zipDelayError(responses, objects -> new ArrayList(Arrays.asList(objects)));
    }

    @CrossOrigin
    @GetMapping("/products/search/{locale}/{catalog}/{productUrl}")
    public Mono<SearchProductDto> searchProduct(@PathVariable String locale, @PathVariable String catalog, @PathVariable String productUrl) {
        return getProductServiceFromCatalog(IdUtils.parse(locale, catalog)).searchProductByUrl(productUrl);
    }

    @CrossOrigin
    @PostMapping("/products/list/update")
    public Mono<List<ProductListItemDto>> updateProducts(@RequestBody List<ProductListItemDto> productList) {
        var responses = new ArrayList<Mono<ProductListItemDto>>();

        for (var item : productList) {
            var productService = getProductServiceFromCatalog(IdUtils.parse(item.getLocale(), item.getCatalog()));
            responses.add(productService.updateProduct(item));
        }

        return Mono.zipDelayError(responses, objects -> new ArrayList(Arrays.asList(objects)));
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

    private ProductService getProductServiceFromCatalog(String catalogAlias) {
        try {
            return productServiceProvider.getServiceFromCatalog(catalogAlias);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s catalog not found", catalogAlias));
        }
    }
}