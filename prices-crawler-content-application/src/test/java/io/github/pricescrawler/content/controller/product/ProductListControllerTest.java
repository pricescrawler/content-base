package io.github.pricescrawler.content.controller.product;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductDto;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext
@AutoConfigureDataMongo
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"ACTIVE_PROFILE=demo"})
class ProductListControllerTest {
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
    void updateProductsTest_OK() {
        var updateProducts = SearchProductDto.builder()
                .locale("local")
                .catalog("demo")
                .product(DemoDataUtils.createProductDto())
                .build();
        new SearchProductDto();

        var entity = restTemplate.postForEntity("/api/v1/products/list/update", List.of(updateProducts), JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }
}
