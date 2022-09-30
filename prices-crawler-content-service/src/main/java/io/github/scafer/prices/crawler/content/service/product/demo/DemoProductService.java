package io.github.scafer.prices.crawler.content.service.product.demo;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductsDto;
import io.github.scafer.prices.crawler.content.common.util.DateTimeUtils;
import io.github.scafer.prices.crawler.content.repository.catalog.CatalogDataService;
import io.github.scafer.prices.crawler.content.repository.product.ProductDataService;
import io.github.scafer.prices.crawler.content.service.product.base.BaseProductService;
import io.github.scafer.prices.crawler.content.service.product.cache.ProductCacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Profile("demo")
@Qualifier("local.demo")
public class DemoProductService extends BaseProductService {
    public DemoProductService(CatalogDataService catalogDataService, ProductDataService productDatabaseService, ProductCacheService productCacheService) {
        super("local", "demo", catalogDataService, productDatabaseService, productCacheService);
    }

    @Override
    protected CompletableFuture<SearchProductsDto> searchItemLogic(String query) {
        var productsResult = getDemoProducts();
        return CompletableFuture.completedFuture(new SearchProductsDto(localeName, catalogName, productsResult, generateCatalogData()));
    }

    @Override
    protected CompletableFuture<SearchProductDto> searchItemByProductUrlLogic(String productUrl) {
        var demoProducts = getDemoProducts();
        return CompletableFuture.completedFuture(new SearchProductDto(localeName, catalogName, demoProducts.get(0)));
    }

    @Override
    protected CompletableFuture<SearchProductDto> updateItemLogic(SearchProductDto query) {
        return CompletableFuture.completedFuture(null);
    }

    private List<ProductDto> getDemoProducts() {
        var product = new ProductDto();
        product.setReference("1");
        product.setName("Demo Product 1");
        product.setBrand("Demo Brand 1");
        product.setQuantity("1 /un");
        product.setDescription("Demo Description 1");
        product.setEanUpcList(List.of("123456789"));
        product.setDate(DateTimeUtils.getCurrentDateTime());
        product.setRegularPrice("1,20€");
        product.setCampaignPrice("1,00€");
        product.setPricePerQuantity("1,00€ /un");
        product.setProductUrl("http://demo-product-1.local");
        product.setImageUrl("http://demo-product-1.png");

        return List.of(product);
    }
}
