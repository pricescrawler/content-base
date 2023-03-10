package io.github.pricescrawler.content.controller.product;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.pricescrawler.content.repository.catalog.CatalogDataRepository;
import io.github.pricescrawler.content.repository.catalog.CategoryDataRepository;
import io.github.pricescrawler.content.repository.catalog.LocaleDataRepository;
import io.github.pricescrawler.content.repository.product.ProductDataRepository;
import io.github.pricescrawler.content.util.DemoDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext
@AutoConfigureDataMongo
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"ACTIVE_PROFILE=demo"})
class ProductHistoryControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LocaleDataRepository localeDataRepository;

    @Autowired
    private CatalogDataRepository catalogDataRepository;

    @Autowired
    private CategoryDataRepository categoryDataRepository;

    @Autowired
    private ProductDataRepository productDataRepository;

    @BeforeEach
    void setup() {
        localeDataRepository.save(DemoDataUtils.getLocaleDao());
        catalogDataRepository.save(DemoDataUtils.getCatalogDao());
        categoryDataRepository.save(DemoDataUtils.getCategoryDao());
        productDataRepository.save(DemoDataUtils.getProductDao());
    }

    @AfterEach()
    void tearDown() {
        localeDataRepository.deleteAll();
        catalogDataRepository.deleteAll();
        categoryDataRepository.deleteAll();
        productDataRepository.deleteAll();
    }

    @Test
    void getProductByEanUpc_OK() {
        var search = String.format("/api/v1/products/history?eanUpc=123456789");
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertFalse(entity.getBody().isEmpty());
    }

    @Test
    void getProductByEanUpc_EMPTY() {
        var search = String.format("/api/v1/products/history?eanUpc=0");
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue(entity.getBody().isEmpty());
    }

    @Test
    void getProductByLocaleAndCatalogAnReferenceTest_OK() {
        var search = String.format("/api/v1/products/history/%s/%s/%s", "local", "demo", "1");
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void getProductByLocaleAndCatalogAnReferenceTest_NOTFOUND() {
        var search = String.format("/api/v1/products/history/%s/%s/%s", "local", "fake-catalog", "1");
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }
}
