package io.github.scafer.prices.crawler.content.controller.product;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductListItemDto;
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
@RequestMapping("/api/v1")
@ConditionalOnProperty("prices.crawler.product.controller.enabled")
public class ProductListController {
    private final ProductServiceProvider productServiceProvider;

    public ProductListController(ProductServiceProvider productServiceProvider) {
        this.productServiceProvider = productServiceProvider;
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

    private ProductService getProductServiceFromCatalog(String catalogAlias) {
        try {
            return productServiceProvider.getServiceFromCatalog(catalogAlias);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s catalog not found", catalogAlias));
        }
    }
}
