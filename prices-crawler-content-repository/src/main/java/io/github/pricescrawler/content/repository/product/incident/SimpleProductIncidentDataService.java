package io.github.pricescrawler.content.repository.product.incident;

import io.github.pricescrawler.content.common.dao.product.PriceDao;
import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import io.github.pricescrawler.content.common.dao.product.incident.ProductIncidentDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.common.util.IdUtils;
import io.github.pricescrawler.content.repository.catalog.CatalogDataService;
import io.github.pricescrawler.content.repository.product.config.ProductDataConfig;
import io.github.pricescrawler.content.repository.product.history.ProductHistoryDataRepository;
import io.github.pricescrawler.content.repository.product.util.ProductUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.Collections;

@Log4j2
@Service
@RequiredArgsConstructor
public class SimpleProductIncidentDataService implements ProductIncidentDataService {
    private final ProductDataConfig productDataConfig;
    private final CatalogDataService catalogDataService;
    private final ProductHistoryDataRepository productHistoryDataRepository;
    private final ProductIncidentDataRepository productIncidentDataRepository;

    @Override
    public Mono<Void> saveIncident(ProductHistoryDao product, ProductDto lastProduct, String query) {
        return productIncidentDataRepository.findById(product.getId())
                .flatMap(incident -> {
                    var lastStoredPrice = incident.getProducts().getLast();
                    return catalogDataService.findLocaleById(IdUtils.extractLocaleFromKey(lastStoredPrice.getId()))
                            .flatMap(locale -> {
                                if (!DateTimeUtils.areDatesOnSameDay(lastStoredPrice.getDate(), lastProduct.getDate(), locale.getTimezone())) {
                                    incident.addProduct(lastProduct);
                                }
                                if (productDataConfig.isHintsEnabled()) {
                                    incident.incrementHits();
                                }
                                if (productDataConfig.isSearchTermsEnabled()) {
                                    incident.setSearchTerms(ProductUtils.parseSearchTerms(incident.getSearchTerms(), query));
                                }
                                return productIncidentDataRepository.save(incident);
                            });
                })
                .switchIfEmpty(productIncidentDataRepository.save(createProductIncident(product.getId(), lastProduct, query)))
                .doOnError(e -> log.error("Error saving product incident. Product ID: {}, Product reference: {}. Error message: {}",
                        product.getId(), lastProduct.getReference(), e.getMessage()))
                .onErrorResume(e -> Mono.empty())
                .then();
    }

    @Override
    public Mono<Boolean> closeIncident(String key, boolean merge) {
        return productIncidentDataRepository.findById(key)
                .flatMap(incident -> merge ? mergeProductIncident(incident) : productIncidentDataRepository.save(incident.closed()).then())
                .then(productIncidentDataRepository.findById(key))
                .map(ProductIncidentDao::isClosed)
                .defaultIfEmpty(false);
    }

    private ProductIncidentDao createProductIncident(String productId, ProductDto lastProduct, String query) {
        var productIncident = ProductIncidentDao.builder()
                .id(productId)
                .products(Collections.singletonList(lastProduct))
                .created(DateTimeUtils.getCurrentDateTime())
                .updated(DateTimeUtils.getCurrentDateTime())
                .build();

        if (productDataConfig.isSearchTermsEnabled()) {
            productIncident.setSearchTerms(ProductUtils.parseSearchTerms(null, query));
        }

        return productIncident;
    }

    private Mono<Void> mergeProductIncident(ProductIncidentDao productIncident) {
        return Mono.zip(
                        productHistoryDataRepository.findById(productIncident.getId()),
                        catalogDataService.findLocaleById(IdUtils.extractLocaleFromKey(productIncident.getId()))
                )
                .flatMap(tuple -> {
                    var product = tuple.getT1();
                    var timezone = tuple.getT2().getTimezone();

                    product.incrementHits(productIncident.getHits());

                    for (var incident : productIncident.getProducts()) {
                        product.updateFromProduct(incident);
                        product.setPrices(ProductUtils.parsePricesHistory(product.getPrices(), new PriceDao(incident), timezone));
                        product.setSearchTerms(ProductUtils.parseSearchTerms(product.getSearchTerms(), null));
                        product.setEanUpcList(ProductUtils.parseEanUpcList(product.getEanUpcList(), incident.getEanUpcList()));
                    }

                    return productHistoryDataRepository.save(product)
                            .then(productIncidentDataRepository.save(productIncident.merged().closed()));
                })
                .then();
    }
}
