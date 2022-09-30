package io.github.scafer.prices.crawler.content.repository.product.incident;

import io.github.scafer.prices.crawler.content.common.dao.product.PriceDao;
import io.github.scafer.prices.crawler.content.common.dao.product.ProductDao;
import io.github.scafer.prices.crawler.content.common.dao.product.incident.ProductIncidentDao;
import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
import io.github.scafer.prices.crawler.content.common.util.DateTimeUtils;
import io.github.scafer.prices.crawler.content.repository.product.ProductDataRepository;
import io.github.scafer.prices.crawler.content.repository.product.util.ProductUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<Void> saveIncident(ProductDao product, ProductDto newProduct, String query) {
        try {
            var optionalIncident = productIncidentDataRepository.findById(product.getId());

            if (optionalIncident.isPresent()) {
                var incident = optionalIncident.get();
                var lastStoredPrice = incident.getProducts().get(incident.getProducts().size() - 1);

                if (!DateTimeUtils.isSameDay(lastStoredPrice.getDate(), newProduct.getDate())) {
                    incident.addNewProduct(newProduct);
                }

                incident.incrementHits();
                incident.setSearchTerms(ProductUtils.parseSearchTerms(incident.getSearchTerms(), query));
                productIncidentDataRepository.save(incident);
            } else {
                var productIncident = new ProductIncidentDao(product.getId(), newProduct);
                productIncident.setSearchTerms(ProductUtils.parseSearchTerms(null, query));
                productIncidentDataRepository.save(productIncident);
            }
        } catch (Exception exception) {
            log.error("Product Incident Exception. Id - {}. Product - {}. Message - {}", product.getId(), newProduct.getReference(), exception.getMessage());
        }

        return null;
    }

    @Override
    public boolean closeIncident(String key, boolean merge) {
        var optionalProductIncident = productIncidentDataRepository.findById(key);

        if (merge && optionalProductIncident.isPresent()) {
            var productIncident = optionalProductIncident.get();
            var optionalProduct = productDataRepository.findById(productIncident.getId());

            if (optionalProduct.isPresent()) {
                var product = optionalProduct.get();
                product.incrementHits(productIncident.getHits());

                for (var newProduct : productIncident.getProducts()) {
                    product.updateFromProduct(newProduct);
                    product.setPrices(ProductUtils.parsePricesHistory(product.getPrices(), new PriceDao(newProduct)));
                    product.setSearchTerms(ProductUtils.parseSearchTerms(product.getSearchTerms(), null));
                    product.setEanUpcList(ProductUtils.parseEanUpcList(product.getEanUpcList(), newProduct.getEanUpcList()));
                }

                productDataRepository.save(product);
                productIncidentDataRepository.save(productIncident.merged().closed());
            }
        } else {
            optionalProductIncident.stream().findFirst().ifPresent(incident -> productIncidentDataRepository.save(incident.closed()));
        }

        return productIncidentDataRepository.findById(key).stream().findFirst().map(ProductIncidentDao::isClosed).orElse(false);
    }
}
