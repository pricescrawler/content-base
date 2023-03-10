package io.github.pricescrawler.content.util;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import io.github.pricescrawler.content.common.dao.catalog.CategoryDao;
import io.github.pricescrawler.content.common.dao.catalog.LocaleDao;
import io.github.pricescrawler.content.common.dao.product.PriceDao;
import io.github.pricescrawler.content.common.dao.product.ProductDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;

import java.util.List;

public class DemoDataUtils {
    public static CatalogDao getCatalogDao() {
        return CatalogDao.builder()
                .id("local.demo")
                .name("Demo")
                .locales(List.of("local"))
                .categories(List.of("demo-category"))
                .build();
    }

    public static CategoryDao getCategoryDao() {
        return CategoryDao.builder()
                .id("demo-category")
                .name("Demo Category")
                .build();
    }

    public static LocaleDao getLocaleDao() {
        return LocaleDao.builder()
                .id("local")
                .name("Locale")
                .build();
    }

    public static ProductDao getProductDao() {
        var price = PriceDao.builder()
                .regularPrice("1,20 €")
                .campaignPrice("1,00 €")
                .pricePerQuantity("1,00 € /un")
                .date(DateTimeUtils.getCurrentDateTime())
                .build();

        return ProductDao.builder()
                .id("local.demo.1")
                .reference("1")
                .locale("local")
                .catalog("demo")
                .name("Demo Product 1")
                .brand("Demo Brand 1")
                .quantity("1 /un")
                .description("Demo Description 1")
                .eanUpcList(List.of("123456789"))
                .productUrl("http://demo-product-1.local")
                .imageUrl("http://demo-product-1.png")
                .prices(List.of(price))
                .build();
    }

    public static ProductDto createProductDto() {
        return ProductDto.builder()
                .reference("1")
                .name("Demo Product 1")
                .brand("Demo Brand 1")
                .quantity("1 /un")
                .description("Demo Description 1")
                .eanUpcList(List.of("123456789"))
                .regularPrice("1,20 €")
                .campaignPrice("1,00 €")
                .pricePerQuantity("1,00 € /un")
                .productUrl("http://demo-product-1.local")
                .imageUrl("http://demo-product-1.png")
                .date(DateTimeUtils.getCurrentDateTime())
                .build();
    }
}
