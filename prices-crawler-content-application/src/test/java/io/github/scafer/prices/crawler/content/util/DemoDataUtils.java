package io.github.scafer.prices.crawler.content.util;

import io.github.scafer.prices.crawler.content.common.dao.catalog.CatalogDao;
import io.github.scafer.prices.crawler.content.common.dao.catalog.CategoryDao;
import io.github.scafer.prices.crawler.content.common.dao.catalog.LocaleDao;
import io.github.scafer.prices.crawler.content.common.dao.product.PriceDao;
import io.github.scafer.prices.crawler.content.common.dao.product.ProductDao;
import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
import io.github.scafer.prices.crawler.content.common.util.DateTimeUtils;

import java.util.List;

public class DemoDataUtils {
    public static CatalogDao getCatalogDao() {
        var catalog = new CatalogDao();
        catalog.setId("local.demo");
        catalog.setName("Demo");
        catalog.setLocales(List.of("local"));
        catalog.setCategories(List.of("demo-category"));
        return catalog;
    }

    public static CategoryDao getCategoryDao() {
        var category = new CategoryDao();
        category.setId("demo-category");
        category.setName("Demo Category");
        return category;
    }

    public static LocaleDao getLocaleDao() {
        var locale = new LocaleDao();
        locale.setId("local");
        locale.setName("Locale");
        return locale;
    }

    public static ProductDao getProductDao() {
        var product = new ProductDao();
        product.setId("local.demo.1");
        product.setReference("1");
        product.setLocale("local");
        product.setCatalog("demo");
        product.setName("Demo Product 1");
        product.setBrand("Demo Brand 1");
        product.setQuantity("1 /un");
        product.setDescription("Demo Description 1");
        product.setEanUpcList(List.of("123456789"));
        product.setProductUrl("http://demo-product-1.local");
        product.setImageUrl("http://demo-product-1.png");
        product.setEanUpcList(List.of("1"));

        var price = new PriceDao();
        price.setRegularPrice("1,20€");
        price.setCampaignPrice("1,00€");
        price.setPricePerQuantity("1,00€ /un");
        price.setDate(DateTimeUtils.getCurrentDateTime());
        product.setPrices(List.of(price));

        return product;
    }

    public static ProductDto createProductDto() {
        var productResult = new ProductDto();
        productResult.setReference("1");
        productResult.setName("Demo Product 1");
        productResult.setBrand("Demo Brand 1");
        productResult.setQuantity("1 /un");
        productResult.setDescription("Demo Description 1");
        productResult.setEanUpcList(List.of("123456789"));
        productResult.setDate(DateTimeUtils.getCurrentDateTime());
        productResult.setRegularPrice("1,20€");
        productResult.setCampaignPrice("1,00€");
        productResult.setPricePerQuantity("1,00€ /un");
        productResult.setProductUrl("http://demo-product-1.local");
        productResult.setImageUrl("http://demo-product-1.png");
        return productResult;
    }
}
