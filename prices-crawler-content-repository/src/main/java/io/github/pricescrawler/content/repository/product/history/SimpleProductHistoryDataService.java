package io.github.pricescrawler.content.repository.product.history;

import io.github.pricescrawler.content.common.dao.product.PriceDao;
import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductsDto;
import io.github.pricescrawler.content.common.util.IdUtils;
import io.github.pricescrawler.content.repository.catalog.CatalogDataService;
import io.github.pricescrawler.content.repository.product.config.ProductDataConfig;
import io.github.pricescrawler.content.repository.product.incident.ProductIncidentDataService;
import io.github.pricescrawler.content.repository.product.util.ProductUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class SimpleProductHistoryDataService implements ProductHistoryDataService {
    private final ProductDataConfig productDataConfig;
    private final CatalogDataService catalogDataService;
    private final ProductIncidentDataService productIncidentDataService;
    private final ProductHistoryDataRepository productHistoryDataRepository;

    @Value("${prices.crawler.product-incident.enabled:true}")
    private boolean isProductIncidentEnabled;

    @Override
    public Mono<ProductHistoryDao> findProduct(String locale, String catalog, String reference) {
        return productHistoryDataRepository.findById(IdUtils.parse(locale, catalog, reference));
    }

    @Override
    public Flux<ProductHistoryDao> findProductsByEanUpc(String eanUpc) {
        return productHistoryDataRepository.findAllByEanUpcList(eanUpc);
    }

    @Override
    public Mono<Void> saveSearchResult(SearchProductsDto searchProducts, String query) {
        return Flux.fromIterable(searchProducts.getProducts())
                .flatMap(productDto -> {
                    var id = IdUtils.parse(searchProducts.getLocale(), searchProducts.getCatalog(), productDto.getReference());
                    return productHistoryDataRepository.findById(id)
                            .flatMap(productData -> {
                                if (isProductDataEquals(productData, productDto)) {
                                    return updatedProductData(productData, productDto, query)
                                            .flatMap(this::saveProduct)
                                            .thenReturn(true);
                                } else {
                                    return productIncidentDataService.saveIncident(productData, productDto, query)
                                            .thenReturn(true);
                                }
                            })
                            .switchIfEmpty(createProductData(searchProducts.getLocale(), searchProducts.getCatalog(), productDto, query).thenReturn(false));
                })
                .then();
    }

    public Mono<Void> saveProduct(ProductHistoryDao product) {
        if (product.getName().isBlank()) {
            return Mono.empty();
        }
        return productHistoryDataRepository.save(product).then();
    }

    private Mono<Void> createProductData(String locale, String catalog, ProductDto product, String query) {
        var productData = new ProductHistoryDao(locale, catalog, product);
        productData.setPrices(List.of(new PriceDao(product)));

        if (productDataConfig.isSearchTermsEnabled()) {
            productData.setSearchTerms(ProductUtils.parseSearchTerms(null, query));
        }

        return saveProduct(productData);
    }

    private Mono<ProductHistoryDao> updatedProductData(ProductHistoryDao product, ProductDto lastProduct, String query) {
        return catalogDataService.findLocaleById(product.getLocale())
                .map(locale -> {
                    product.setPrices(ProductUtils.parsePricesHistory(product.getPrices(), new PriceDao(lastProduct), locale.getTimezone()));
                    product.setEanUpcList(ProductUtils.parseEanUpcList(product.getEanUpcList(), lastProduct.getEanUpcList()));

                    if (productDataConfig.isHintsEnabled()) {
                        product.incrementHits();
                    }

                    if (productDataConfig.isSearchTermsEnabled()) {
                        product.setSearchTerms(ProductUtils.parseSearchTerms(product.getSearchTerms(), query));
                    }

                    return product;
                });
    }

    private boolean isProductDataEquals(ProductHistoryDao product, ProductDto lastProduct) {
        if (!isProductIncidentEnabled) {
            return true;
        }

        return (product.getName() == null || product.getName().equalsIgnoreCase(lastProduct.getName())) &&
                (product.getBrand() == null || product.getBrand().equalsIgnoreCase(lastProduct.getBrand())) &&
                (product.getProductUrl() == null || product.getProductUrl().equalsIgnoreCase(lastProduct.getProductUrl())) &&
                (product.getDescription() == null || product.getDescription().equalsIgnoreCase(lastProduct.getDescription())) &&
                (product.getEanUpcList() == null || lastProduct.getEanUpcList() == null || new HashSet<>(product.getEanUpcList()).containsAll(lastProduct.getEanUpcList()));
    }
}
