package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.service.product.ProductService;
import io.github.pricescrawler.content.service.product.provider.ProductServiceProvider;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@UtilityClass
class ProductServiceResolver {

    /**
     * Resolves the product service of a catalog, answering {@code 404} when no service
     * is registered for it.
     */
    static ProductService resolve(ProductServiceProvider productServiceProvider, String catalogAlias) {
        return productServiceProvider.findServiceFromCatalog(catalogAlias)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s catalog not found", catalogAlias)));
    }
}
