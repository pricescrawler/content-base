package io.github.scafer.prices.crawler.content.repository.product;

import io.github.scafer.prices.crawler.content.common.dao.product.PriceDao;
import io.github.scafer.prices.crawler.content.common.dao.product.ProductDao;
import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductsDto;
import io.github.scafer.prices.crawler.content.common.util.IdUtils;
import io.github.scafer.prices.crawler.content.repository.product.incident.ProductIncidentDataService;
import io.github.scafer.prices.crawler.content.repository.product.util.ProductUtils;
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
    private final ProductDataRepository productDataRepository;
    private final ProductIncidentDataService productIncidentDataService;

    @Value("${prices.crawler.product-incident.enabled:true}")
    private boolean isProductIncidentEnabled;

    public SimpleProductDataService(ProductDataRepository productDataRepository, ProductIncidentDataService productIncidentDataService) {
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
    public CompletableFuture<Void> saveSearchResult(SearchProductsDto searchProducts, String query) {
        for (var productDto : searchProducts.getProducts()) {
            var optionalProduct = productDataRepository.findById(IdUtils.parse(searchProducts.getLocale(), searchProducts.getCatalog(), productDto.getReference()));

            if (optionalProduct.isPresent()) {
                var productData = optionalProduct.get();

                if (isProductDataEquals(productData, productDto)) {
                    productDataRepository.save(updatedProductData(productData, productDto, query));
                } else {
                    CompletableFuture.supplyAsync(() -> productIncidentDataService.saveIncident(productData, productDto, query));
                }
            } else {
                createProductData(searchProducts.getLocale(), searchProducts.getCatalog(), productDto, query);
            }
        }

        return null;
    }

    private void createProductData(String locale, String catalog, ProductDto product, String query) {
        var productData = new ProductDao(locale, catalog, product);
        productData.setPrices(List.of(new PriceDao(product)));
        productData.setSearchTerms(ProductUtils.parseSearchTerms(null, query));
        productDataRepository.save(productData);
    }

    private ProductDao updatedProductData(ProductDao product, ProductDto lastProduct, String query) {
        product.updateFromProduct(lastProduct).incrementHits();
        product.setPrices(ProductUtils.parsePricesHistory(product.getPrices(), new PriceDao(lastProduct)));
        product.setSearchTerms(ProductUtils.parseSearchTerms(product.getSearchTerms(), query));
        product.setEanUpcList(ProductUtils.parseEanUpcList(product.getEanUpcList(), lastProduct.getEanUpcList()));
        return product;
    }

    private boolean isProductDataEquals(ProductDao product, ProductDto lastProduct) {
        return isProductIncidentEnabled &&
                (product.getName() == null || product.getName().equalsIgnoreCase(lastProduct.getName())) &&
                (product.getBrand() == null || product.getBrand().equalsIgnoreCase(lastProduct.getBrand())) &&
                (product.getProductUrl() == null || product.getProductUrl().equalsIgnoreCase(lastProduct.getProductUrl())) &&
                (product.getDescription() == null || product.getDescription().equalsIgnoreCase(lastProduct.getDescription())) &&
                (product.getEanUpcList() == null || new HashSet<>(product.getEanUpcList()).containsAll(lastProduct.getEanUpcList()));
    }
}
