package io.github.scafer.prices.crawler.content.controller.product;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductListItemDto;
import io.github.scafer.prices.crawler.content.common.dto.product.ProductListShareDto;
import io.github.scafer.prices.crawler.content.common.util.IdUtils;
import io.github.scafer.prices.crawler.content.service.product.ProductService;
import io.github.scafer.prices.crawler.content.service.product.list.ProductListService;
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
@RequestMapping("/api/v1/products/list")
@ConditionalOnProperty("prices.crawler.controller.product.list.enabled")
public class ProductListController {
    private final ProductServiceProvider productServiceProvider;
    private final ProductListService productListService;

    public ProductListController(ProductServiceProvider productServiceProvider, ProductListService productListService) {
        this.productServiceProvider = productServiceProvider;
        this.productListService = productListService;
    }

    @CrossOrigin
    @PostMapping("/update")
    public Mono<List<ProductListItemDto>> updateProductList(@RequestBody List<ProductListItemDto> productListItems) {
        var responses = new ArrayList<Mono<ProductListItemDto>>();

        for (var item : productListItems) {
            var productService = getProductServiceFromCatalog(IdUtils.parse(item.getLocale(), item.getCatalog()));
            responses.add(productService.updateProductListItem(item));
        }

        return Mono.zipDelayError(responses, objects -> new ArrayList(Arrays.asList(objects)));
    }

    @CrossOrigin
    @PostMapping("/store")
    public ProductListShareDto storeProductList(@RequestBody List<ProductListItemDto> productListItems) {
        return productListService.storeProductList(productListItems);
    }

    @CrossOrigin
    @GetMapping
    public List<ProductListItemDto> retrieveProductList(@RequestParam String id) {
        return productListService.retrieveProductList(id);
    }

    private ProductService getProductServiceFromCatalog(String catalogAlias) {
        try {
            return productServiceProvider.getServiceFromCatalog(catalogAlias);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s catalog not found", catalogAlias));
        }
    }
}
