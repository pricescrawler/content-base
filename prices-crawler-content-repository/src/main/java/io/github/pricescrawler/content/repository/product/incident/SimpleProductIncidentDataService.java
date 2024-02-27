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
    public void saveIncident(ProductHistoryDao product, ProductDto lastProduct, String query) {
        try {
            var optionalIncident = productIncidentDataRepository.findById(product.getId());

            if (optionalIncident.isPresent()) {
                var incident = optionalIncident.get();
                var lastStoredPrice = incident.getProducts().getLast();
                var timezone = catalogDataService.findLocaleById(IdUtils.extractLocaleFromKey(lastStoredPrice.getId()))
                        .orElseThrow().getTimezone();

                if (!DateTimeUtils.areDatesOnSameDay(lastStoredPrice.getDate(), lastProduct.getDate(), timezone)) {
                    incident.addProduct(lastProduct);
                }

                if (productDataConfig.isHintsEnabled()) {
                    incident.incrementHits();
                }

                if (productDataConfig.isSearchTermsEnabled()) {
                    incident.setSearchTerms(ProductUtils.parseSearchTerms(incident.getSearchTerms(), query));
                }

                productIncidentDataRepository.save(incident);
            } else {
                productIncidentDataRepository.save(createProductIncident(product.getId(), lastProduct, query));
            }
        } catch (Exception e) {
            log.error("Error saving product incident. Product ID: {}, Product reference: {}. Error message: {}", product.getId(), lastProduct.getReference(), e.getMessage());
        }
    }

    @Override
    public boolean closeIncident(String key, boolean merge) {
        var optionalProductIncident = productIncidentDataRepository.findById(key);

        if (merge && optionalProductIncident.isPresent()) {
            mergeProductIncident(optionalProductIncident.get());
        } else {
            optionalProductIncident.ifPresent(incident -> productIncidentDataRepository.save(incident.closed()));
        }

        return productIncidentDataRepository.findById(key)
                .map(ProductIncidentDao::isClosed)
                .orElse(false);
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

    private void mergeProductIncident(ProductIncidentDao productIncident) {
        var optionalProduct = productHistoryDataRepository.findById(productIncident.getId());
        var timezone = catalogDataService.findLocaleById(IdUtils.extractLocaleFromKey(productIncident.getId()))
                .orElseThrow().getTimezone();

        optionalProduct.ifPresent(product -> {
            product.incrementHits(productIncident.getHits());

            for (var incident : productIncident.getProducts()) {
                product.updateFromProduct(incident);
                product.setPrices(ProductUtils.parsePricesHistory(product.getPrices(), new PriceDao(incident), timezone));
                product.setSearchTerms(ProductUtils.parseSearchTerms(product.getSearchTerms(), null));
                product.setEanUpcList(ProductUtils.parseEanUpcList(product.getEanUpcList(), incident.getEanUpcList()));
            }

            productHistoryDataRepository.save(product);
            productIncidentDataRepository.save(productIncident.merged().closed());
        });
    }
}
