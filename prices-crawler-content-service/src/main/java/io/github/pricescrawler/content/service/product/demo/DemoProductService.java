package io.github.pricescrawler.content.service.product.demo;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByQueryDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByUrlDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductsDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.common.util.IdUtils;
import io.github.pricescrawler.content.repository.catalog.CatalogDataService;
import io.github.pricescrawler.content.repository.product.ProductDataService;
import io.github.pricescrawler.content.repository.product.history.ProductHistoryDataService;
import io.github.pricescrawler.content.service.product.base.BaseProductService;
import io.github.pricescrawler.content.service.product.cache.ProductCacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Profile("demo")
@Qualifier("local.demo")
public class DemoProductService extends BaseProductService {
    public DemoProductService(CatalogDataService catalogDataService,
                              ProductDataService productDataService,
                              ProductCacheService productCacheService,
                              ProductHistoryDataService productHistoryDataService) {
        super("local", "demo", catalogDataService, productDataService, productCacheService, productHistoryDataService);
    }

    @Override
    protected CompletableFuture<SearchProductsDto> searchItemLogic(FilterProductByQueryDto filterProduct) {
        var productsResult = parseProductsFromContent(filterProduct.getComposedCatalogKey(), null,
                DateTimeUtils.getCurrentDateTime());
        return CompletableFuture.completedFuture(new SearchProductsDto(localeId, filterProduct.getComposedCatalogKey(),
                productsResult, generateCatalogData(filterProduct.getStoreId())));
    }

    @Override
    protected CompletableFuture<SearchProductDto> searchItemByProductUrlLogic(FilterProductByUrlDto filterProductByUrl) {
        var demoProduct = parseProductFromContent(filterProductByUrl.getComposedCatalogKey(),
                filterProductByUrl.getUrl(), null, DateTimeUtils.getCurrentDateTime());
        return CompletableFuture.completedFuture(new SearchProductDto(localeId,
                filterProductByUrl.getComposedCatalogKey(), demoProduct));
    }

    @Override
    protected CompletableFuture<ProductListItemDto> updateItemLogic(ProductListItemDto productListItem) {
        return CompletableFuture.completedFuture(productListItem);
    }

    @Override
    public List<ProductDto> parseProductsFromContent(String catalogKey, String content, String dateTime) {
        return List.of(parseProductFromContent(catalogKey, null, content, dateTime));
    }

    @Override
    public ProductDto parseProductFromContent(String catalogKey, String query, String content, String dateTime) {
        return ProductDto.builder()
                .id(IdUtils.parse(localeId, catalogKey, "1"))
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
