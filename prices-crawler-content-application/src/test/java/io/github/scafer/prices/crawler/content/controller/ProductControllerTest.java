package io.github.scafer.prices.crawler.content.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.MongoClient;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductsDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchQueryDto;
import io.github.scafer.prices.crawler.content.repository.catalog.CatalogDataRepository;
import io.github.scafer.prices.crawler.content.repository.catalog.CategoryDataRepository;
import io.github.scafer.prices.crawler.content.repository.catalog.LocaleDataRepository;
import io.github.scafer.prices.crawler.content.repository.product.ProductDataService;
import io.github.scafer.prices.crawler.content.util.DemoDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"ACTIVE_PROFILE=demo"})
class ProductControllerTest {
    private final String PRODUCTS_SEARCH = "/api/v1/products/search";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ProductDataService productDataService;

    @Autowired
    private LocaleDataRepository localeDataRepository;

    @Autowired
    private CatalogDataRepository catalogDataRepository;

    @Autowired
    private CategoryDataRepository categoryDataRepository;

    @BeforeEach
    void setup() {
        localeDataRepository.save(DemoDataUtils.getLocaleDao());
        catalogDataRepository.save(DemoDataUtils.getCatalogDao());
        categoryDataRepository.save(DemoDataUtils.getCategoryDao());
    }

    @AfterEach()
    void tearDown() {
        localeDataRepository.deleteAll();
        catalogDataRepository.deleteAll();
        categoryDataRepository.deleteAll();
    }

    @Test
    void productByQueryTest_OK() {
        var search = new SearchQueryDto(new String[]{"local.demo"}, "query");
        var entity = restTemplate.postForEntity(PRODUCTS_SEARCH, search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void productByQueryTest_NOTFOUND() {
        var search = new SearchQueryDto(new String[]{"local.fake-catalog"}, "product_01");
        var entity = restTemplate.postForEntity(PRODUCTS_SEARCH, search, JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    void productByUrlTest_OK() {
        var search = String.format("/api/v1/products/search/%s/%s/%s", "local", "demo", "url.local");
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void productByUrlTest_NOTFOUND() {
        var search = String.format("/api/v1/products/search/%s/%s/%s", "local", "fake-catalog", "url.local");
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    void updateProductsTest_OK() {
        var updateProducts = new SearchProductDto();
        updateProducts.setLocale("local");
        updateProducts.setCatalog("demo");
        updateProducts.setProduct(DemoDataUtils.createProductDto());
        var entity = restTemplate.postForEntity("/api/v1/products/list/update", List.of(updateProducts), JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void getProductByLocaleAndCatalogAnReferenceTest_OK() {
        createDemoProductData();
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

    private void createDemoProductData() {
        var searchResult = new SearchProductsDto("local", "demo", List.of(DemoDataUtils.createProductDto()), null);
        productDataService.saveSearchResult(searchResult, "demo");
        var mongodbCollection = mongoClient.getDatabase("prices-crawler-db").getCollection("products");
        assertEquals(1, mongodbCollection.countDocuments());
    }
}
