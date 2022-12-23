package io.github.scafer.prices.crawler.content.controller.product;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchQueryDto;
import io.github.scafer.prices.crawler.content.repository.catalog.CatalogDataRepository;
import io.github.scafer.prices.crawler.content.repository.catalog.CategoryDataRepository;
import io.github.scafer.prices.crawler.content.repository.catalog.LocaleDataRepository;
import io.github.scafer.prices.crawler.content.repository.product.ProductDataRepository;
import io.github.scafer.prices.crawler.content.util.DemoDataUtils;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext
@AutoConfigureDataMongo
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"ACTIVE_PROFILE=demo"})
public class ProductSearchControllerTest {
    private final String PRODUCTS_SEARCH = "/api/v1/products/search";

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
}
