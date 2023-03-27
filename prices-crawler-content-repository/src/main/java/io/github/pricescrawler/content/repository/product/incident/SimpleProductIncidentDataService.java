package io.github.pricescrawler.content.repository.product.incident;

import io.github.pricescrawler.content.common.dao.product.PriceDao;
import io.github.pricescrawler.content.common.dao.product.ProductDao;
import io.github.pricescrawler.content.common.dao.product.incident.ProductIncidentDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.repository.product.ProductDataRepository;
import io.github.pricescrawler.content.repository.product.util.ProductUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Log4j2
@Service
public class SimpleProductIncidentDataService implements ProductIncidentDataService {
    private final ProductIncidentDataRepository productIncidentDataRepository;
    private final ProductDataRepository productDataRepository;

    public SimpleProductIncidentDataService(ProductIncidentDataRepository productIncidentDataRepository, ProductDataRepository productDataRepository) {
        this.productIncidentDataRepository = productIncidentDataRepository;
        this.productDataRepository = productDataRepository;
    }

    @Override
    public void saveIncident(ProductDao product, ProductDto lastProduct, String query) {
        try {
            var optionalIncident = productIncidentDataRepository.findById(product.getId());

            if (optionalIncident.isPresent()) {
                ProductIncidentDao incident = optionalIncident.get();
                ProductDto lastStoredPrice = incident.getProducts().get(incident.getProducts().size() - 1);

                if (!DateTimeUtils.areDatesOnSameDay(lastStoredPrice.getDate(), lastProduct.getDate())) {
                    incident.addProduct(lastProduct);
                }

                incident.incrementHits();
                incident.setSearchTerms(ProductUtils.parseSearchTerms(incident.getSearchTerms(), query));
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
        return ProductIncidentDao.builder()
                .id(productId)
                .products(Collections.singletonList(lastProduct))
                .searchTerms(ProductUtils.parseSearchTerms(null, query))
                .created(DateTimeUtils.getCurrentDateTime())
                .updated(DateTimeUtils.getCurrentDateTime())
                .build();
    }

    private void mergeProductIncident(ProductIncidentDao productIncident) {
        var optionalProduct = productDataRepository.findById(productIncident.getId());

        optionalProduct.ifPresent(product -> {
            product.incrementHits(productIncident.getHits());

            for (var incident : productIncident.getProducts()) {
                product.updateFromProduct(incident);
                product.setPrices(ProductUtils.parsePricesHistory(product.getPrices(), new PriceDao(incident)));
                product.setSearchTerms(ProductUtils.parseSearchTerms(product.getSearchTerms(), null));
                product.setEanUpcList(ProductUtils.parseEanUpcList(product.getEanUpcList(), incident.getEanUpcList()));
            }

            productDataRepository.save(product);
            productIncidentDataRepository.save(productIncident.merged().closed());
        });
    }
}