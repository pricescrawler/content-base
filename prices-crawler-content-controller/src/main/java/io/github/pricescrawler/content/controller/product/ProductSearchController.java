package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dto.product.search.SearchProductDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductsDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchQueryDto;
import io.github.pricescrawler.content.common.util.IdUtils;
import io.github.pricescrawler.content.service.product.ProductService;
import io.github.pricescrawler.content.service.product.provider.ProductServiceProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/products/search")
@ConditionalOnProperty("prices.crawler.controller.product.search.enabled")
public class ProductSearchController {
    private final ProductServiceProvider productServiceProvider;

    public ProductSearchController(ProductServiceProvider productServiceProvider) {
        this.productServiceProvider = productServiceProvider;
    }

    @PostMapping
    public Flux<SearchProductsDto> searchProducts(@RequestBody SearchQueryDto searchQuery) {
        var searchResults = Arrays.stream(searchQuery.getCatalogs())
                .map(catalog -> getProductServiceFromCatalog(catalog).searchProductByQuery(searchQuery.getQuery()))
                .toList();

        return Flux.merge(searchResults);
    }

    @GetMapping("/{locale}/{catalog}/{productUrl}")
    public Mono<SearchProductDto> searchProduct(@PathVariable String locale, @PathVariable String catalog, @PathVariable String productUrl) {
        return getProductServiceFromCatalog(IdUtils.parse(locale, catalog)).searchProductByProductUrl(productUrl);
    }

    private ProductService getProductServiceFromCatalog(String catalogAlias) {
        try {
            return productServiceProvider.getServiceFromCatalog(catalogAlias);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s catalog not found", catalogAlias));
        }
    }
}