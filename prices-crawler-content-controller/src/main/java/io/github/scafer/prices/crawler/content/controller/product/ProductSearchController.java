package io.github.scafer.prices.crawler.content.controller.product;

import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductsDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchQueryDto;
import io.github.scafer.prices.crawler.content.common.util.IdUtils;
import io.github.scafer.prices.crawler.content.service.product.ProductService;
import io.github.scafer.prices.crawler.content.service.product.provider.ProductServiceProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products/search")
@ConditionalOnProperty("prices.crawler.controller.product.search.enabled")
public class ProductSearchController {
    private final ProductServiceProvider productServiceProvider;

    public ProductSearchController(ProductServiceProvider productServiceProvider) {
        this.productServiceProvider = productServiceProvider;
    }

    @CrossOrigin
    @PostMapping
    public Mono<List<SearchProductsDto>> searchProducts(@RequestBody SearchQueryDto searchQuery) {
        var responses = new ArrayList<Mono<SearchProductsDto>>();

        for (String catalog : searchQuery.getCatalogs()) {
            var productService = getProductServiceFromCatalog(catalog);
            responses.add(productService.searchProductByQuery(searchQuery.getQuery()));
        }

        return Mono.zipDelayError(responses, objects -> new ArrayList(Arrays.asList(objects)));
    }

    @CrossOrigin
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
