package io.github.pricescrawler.content.repository.product;

import io.github.pricescrawler.content.common.dao.product.PriceDao;
import io.github.pricescrawler.content.common.dao.product.ProductDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductsDto;
import io.github.pricescrawler.content.common.util.IdUtils;
import io.github.pricescrawler.content.repository.product.config.ProductDataConfig;
import io.github.pricescrawler.content.repository.product.incident.ProductIncidentDataService;
import io.github.pricescrawler.content.repository.product.util.ProductUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
public class SimpleProductDataService implements ProductDataService {
    private final ProductDataConfig productDataConfig;
    private final ProductDataRepository productDataRepository;
    private final ProductIncidentDataService productIncidentDataService;

    @Value("${prices.crawler.product-incident.enabled:true}")
    private boolean isProductIncidentEnabled;

    public SimpleProductDataService(ProductDataConfig productDataConfig, ProductDataRepository productDataRepository, ProductIncidentDataService productIncidentDataService) {
        this.productDataConfig = productDataConfig;
        this.productDataRepository = productDataRepository;
        this.productIncidentDataService = productIncidentDataService;
    }

    @Override
    public Optional<ProductDao> findProduct(String locale, String catalog, String reference) {
        return productDataRepository.findById(IdUtils.parse(locale, catalog, reference));
    }

    @Override
    public List<ProductDao> findProductsByEanUpc(String eanUpc) {
        return productDataRepository.findAllByEanUpcList(eanUpc);
    }

    @Override
    public void saveSearchResult(SearchProductsDto searchProducts, String query) {
        for (var productDto : searchProducts.getProducts()) {
            var optionalProduct = productDataRepository.findById(IdUtils.parse(searchProducts.getLocale(), searchProducts.getCatalog(), productDto.getReference()));

            if (optionalProduct.isPresent()) {
                var productData = optionalProduct.get();

                if (isProductDataEquals(productData, productDto)) {
                    productDataRepository.save(updatedProductData(productData, productDto, query));
                } else {
                    CompletableFuture.runAsync(() -> productIncidentDataService.saveIncident(productData, productDto, query));
                }
            } else {
                createProductData(searchProducts.getLocale(), searchProducts.getCatalog(), productDto, query);
            }
        }
    }

    private void createProductData(String locale, String catalog, ProductDto product, String query) {
        var productData = new ProductDao(locale, catalog, product);
        productData.setPrices(List.of(new PriceDao(product)));

        if (productDataConfig.isSearchTermsEnabled()) {
            productData.setSearchTerms(ProductUtils.parseSearchTerms(null, query));
        }

        productDataRepository.save(productData);
    }

    private ProductDao updatedProductData(ProductDao product, ProductDto lastProduct, String query) {
        product.setPrices(ProductUtils.parsePricesHistory(product.getPrices(), new PriceDao(lastProduct)));
        product.setEanUpcList(ProductUtils.parseEanUpcList(product.getEanUpcList(), lastProduct.getEanUpcList()));

        if (productDataConfig.isHintsEnabled()) {
            product.incrementHits();
        }

        if (productDataConfig.isSearchTermsEnabled()) {
            product.setSearchTerms(ProductUtils.parseSearchTerms(product.getSearchTerms(), query));
        }
        return product;
    }

    private boolean isProductDataEquals(ProductDao product, ProductDto lastProduct) {
        return isProductIncidentEnabled &&
                (product.getName() == null || product.getName().equalsIgnoreCase(lastProduct.getName())) &&
                (product.getBrand() == null || product.getBrand().equalsIgnoreCase(lastProduct.getBrand())) &&
                (product.getProductUrl() == null || product.getProductUrl().equalsIgnoreCase(lastProduct.getProductUrl())) &&
                (product.getDescription() == null || product.getDescription().equalsIgnoreCase(lastProduct.getDescription())) &&
                (product.getEanUpcList() == null || lastProduct.getEanUpcList() == null || new HashSet<>(product.getEanUpcList()).containsAll(lastProduct.getEanUpcList()));
    }
}
