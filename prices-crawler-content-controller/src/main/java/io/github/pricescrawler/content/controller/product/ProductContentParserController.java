package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.parser.RawProductContentDto;
import io.github.pricescrawler.content.service.product.ProductService;
import io.github.pricescrawler.content.service.product.provider.ProductServiceProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/products/parser")
@ConditionalOnProperty("prices.crawler.controller.product.parser.enabled")
public class ProductContentParserController {
    private final ProductServiceProvider productServiceProvider;

    public ProductContentParserController(ProductServiceProvider productServiceProvider) {
        this.productServiceProvider = productServiceProvider;
    }

    @PostMapping
    public ProductDto parseProductFromContent(@RequestBody RawProductContentDto rawProductContent) {
        var productService = getProductServiceFromCatalog(rawProductContent.getCatalog());
        return productService.parseProductFromContent(rawProductContent.getCatalog(), rawProductContent.getUrl(),
                rawProductContent.getContent(), rawProductContent.getDate());
    }

    @PostMapping("/list")
    public List<ProductDto> parseProductListFromContent(@RequestBody RawProductContentDto rawProductContent) {
        var productService = getProductServiceFromCatalog(rawProductContent.getCatalog());
        return productService.parseProductsFromContent(rawProductContent.getCatalog(), rawProductContent.getContent(),
                rawProductContent.getDate());
    }

    private ProductService getProductServiceFromCatalog(String catalogAlias) {
        try {
            return productServiceProvider.getServiceFromCatalog(catalogAlias);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s catalog not found",
                    catalogAlias));
        }
    }
}
