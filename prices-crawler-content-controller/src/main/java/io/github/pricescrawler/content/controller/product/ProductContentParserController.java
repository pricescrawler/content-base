package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.parser.ProductContentDto;
import io.github.pricescrawler.content.common.util.IdUtils;
import io.github.pricescrawler.content.repository.catalog.CatalogDataService;
import io.github.pricescrawler.content.service.product.provider.ProductServiceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Accepts pre-fetched page content (typically HTML) from a client and runs it through
 * a catalog's parser without the backend making any request to the catalog itself. Only
 * catalogs with {@code isClientFetchRequired: true} may use this — everything else must
 * go through {@link ProductSearchController}, which fetches server-side. This
 * restriction exists because the endpoint is otherwise a generic "parse arbitrary
 * content as if it came from catalog X" primitive; gating it keeps it scoped to the
 * catalogs that actually need a client-side fetch (e.g. sites whose bot protection
 * blocks the backend's own datacenter IP).
 */
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/parser")
@ConditionalOnProperty("prices.crawler.controller.product.parser.enabled")
public class ProductContentParserController {
    private final ProductServiceProvider productServiceProvider;
    private final CatalogDataService catalogDataService;

    @PostMapping
    public Mono<ProductDto> parseProductFromContent(@RequestBody ProductContentDto rawProductContent) {
        return authorizeClientFetch(rawProductContent.getCatalog())
                .map(_ -> ProductServiceResolver.resolve(productServiceProvider, rawProductContent.getCatalog())
                        .parseProductFromContent(rawProductContent.getCatalog(), rawProductContent.getUrl(),
                                rawProductContent.getContent(), rawProductContent.getDate()));
    }

    @PostMapping("/list")
    public Mono<List<ProductDto>> parseProductListFromContent(@RequestBody ProductContentDto rawProductContent) {
        return authorizeClientFetch(rawProductContent.getCatalog())
                .map(_ -> ProductServiceResolver.resolve(productServiceProvider, rawProductContent.getCatalog())
                        .parseProductsFromContent(rawProductContent.getCatalog(), rawProductContent.getContent(),
                                rawProductContent.getDate()));
    }

    /**
     * Resolves the catalog behind a composed {@code locale.catalog} key and errors
     * unless it opts into client-supplied content ({@code 404} when the catalog
     * doesn't exist, {@code 403} when it exists but hasn't opted in).
     */
    private Mono<CatalogDao> authorizeClientFetch(String composedCatalogKey) {
        var localeId = IdUtils.extractLocaleFromKey(composedCatalogKey);
        var catalogId = IdUtils.extractCatalogFromComposedKey(IdUtils.removeLocaleFromComposedKey(composedCatalogKey));

        return catalogDataService.findCatalogByIdAndLocaleId(catalogId, localeId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s catalog not found", composedCatalogKey))))
                .filter(CatalogDao::isClientFetchRequired)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN,
                        String.format("%s catalog does not accept client-supplied content", composedCatalogKey))));
    }
}
