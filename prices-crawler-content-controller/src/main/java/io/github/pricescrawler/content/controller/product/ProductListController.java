package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import io.github.pricescrawler.content.common.dto.product.ProductListShareDto;
import io.github.pricescrawler.content.common.util.IdUtils;
import io.github.pricescrawler.content.service.product.ProductService;
import io.github.pricescrawler.content.service.product.list.ProductListService;
import io.github.pricescrawler.content.service.product.provider.ProductServiceProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.util.List;

@CrossOrigin
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

    @PostMapping("/update")
    public Flux<ProductListItemDto> updateProductList(@RequestBody List<ProductListItemDto> productListItems) {
        if (productListItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The list is empty");
        }

        return Flux.fromIterable(productListItems)
                .flatMap(item -> getProductServiceFromCatalog(IdUtils.parse(item.getLocale(), item.getCatalog())).updateProductListItem(item));
    }

    @PostMapping("/store")
    public ProductListShareDto storeProductList(@RequestBody List<ProductListItemDto> productListItems) {
        return productListService.storeProductList(productListItems);
    }

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
