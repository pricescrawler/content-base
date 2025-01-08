package io.github.pricescrawler.content.util;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import io.github.pricescrawler.content.common.dao.catalog.CategoryDao;
import io.github.pricescrawler.content.common.dao.catalog.LocaleDao;
import io.github.pricescrawler.content.common.dao.product.PriceDao;
import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.repository.catalog.CatalogDataRepository;
import io.github.pricescrawler.content.repository.catalog.CategoryDataRepository;
import io.github.pricescrawler.content.repository.catalog.LocaleDataRepository;
import io.github.pricescrawler.content.repository.product.history.ProductHistoryDataRepository;
import io.github.pricescrawler.content.repository.product.list.ProductListDataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"ACTIVE_PROFILE=demo"})
public abstract class BaseSpringBootTest extends MongoContainerTest {
    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected LocaleDataRepository localeDataRepository;

    @Autowired
    protected CatalogDataRepository catalogDataRepository;

    @Autowired
    protected CategoryDataRepository categoryDataRepository;

    @Autowired
    protected ProductHistoryDataRepository productHistoryDataRepository;

    @Autowired
    protected ProductListDataRepository productListDataRepository;

    @BeforeEach
    void setup() {
        localeDataRepository.save(getLocaleDao());
        catalogDataRepository.save(getCatalogDao());
        categoryDataRepository.save(getCategoryDao());
        productHistoryDataRepository.save(getProductDao());
    }

    @AfterEach
    void tearDown() {
        localeDataRepository.deleteAll();
        catalogDataRepository.deleteAll();
        categoryDataRepository.deleteAll();
        productHistoryDataRepository.deleteAll();
    }

    protected CatalogDao getCatalogDao() {
        return CatalogDao.builder()
                .id("local.demo")
                .name("Demo")
                .locales(List.of("local"))
                .categories(List.of("demo-category"))
                .build();
    }

    protected CategoryDao getCategoryDao() {
        return CategoryDao.builder()
                .id("demo-category")
                .name("Demo Category")
                .build();
    }

    protected LocaleDao getLocaleDao() {
        return LocaleDao.builder()
                .id("local")
                .name("Locale")
                .build();
    }

    protected ProductHistoryDao getProductDao() {
        var price = PriceDao.builder()
                .regularPrice("1,20 €")
                .campaignPrice("1,00 €")
                .pricePerQuantity("1,00 € /un")
                .date(DateTimeUtils.getCurrentDateTime())
                .build();

        return ProductHistoryDao.builder()
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

    protected ProductDto createProductDto() {
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
