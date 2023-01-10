package io.github.scafer.prices.crawler.content.controller.product;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.scafer.prices.crawler.content.common.dto.product.parser.RawProductContentDto;
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
class ProductContentParserControllerTest {
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
    void parseProductFromContent_OK() {
        var body = RawProductContentDto.builder().catalog("local.demo").build();
        var entity = restTemplate.postForEntity("/api/v1/products/parser", body, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void parseProductListFromContent_OK() {
        var body = RawProductContentDto.builder().catalog("local.demo").build();
        var entity = restTemplate.postForEntity("/api/v1/products/parser/list", body, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }
}
