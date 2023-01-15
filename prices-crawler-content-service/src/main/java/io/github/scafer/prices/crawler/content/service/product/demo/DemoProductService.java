package io.github.scafer.prices.crawler.content.service.product.demo;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
import io.github.scafer.prices.crawler.content.common.dto.product.ProductListItemDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductsDto;
import io.github.scafer.prices.crawler.content.common.util.DateTimeUtils;
import io.github.scafer.prices.crawler.content.common.util.IdUtils;
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
        var productsResult = parseProductsFromContent(null, DateTimeUtils.getCurrentDateTime());
        return CompletableFuture.completedFuture(new SearchProductsDto(localeName, catalogName, productsResult, generateCatalogData()));
    }

    @Override
    protected CompletableFuture<SearchProductDto> searchItemByProductUrlLogic(String productUrl) {
        var demoProduct = parseProductFromContent(null, productUrl, DateTimeUtils.getCurrentDateTime());
        return CompletableFuture.completedFuture(new SearchProductDto(localeName, catalogName, demoProduct));
    }

    @Override
    protected CompletableFuture<ProductListItemDto> updateItemLogic(ProductListItemDto productListItem) {
        return CompletableFuture.completedFuture(productListItem);
    }

    @Override
    public List<ProductDto> parseProductsFromContent(String content, String dateTime) {
        return List.of(parseProductFromContent(content, null, dateTime));
    }

    @Override
    public ProductDto parseProductFromContent(String content, String query, String dateTime) {
        return ProductDto.builder()
                .id(IdUtils.parse(localeName, catalogName, "1"))
                .reference("1")
                .name("Demo Product 1")
                .brand("Demo Brand 1")
                .quantity("1 /un")
                .description("Demo Description 1")
                .eanUpcList(List.of("123456789"))
                .date(dateTime)
                .regularPrice("1,20 €")
                .campaignPrice("1,00 €")
                .pricePerQuantity("1,00 € /un")
                .productUrl("http://demo-product-1.local")
                .imageUrl("http://demo-product-1.png")
                .build();
    }
}
