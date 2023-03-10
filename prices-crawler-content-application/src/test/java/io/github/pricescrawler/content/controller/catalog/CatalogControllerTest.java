package io.github.pricescrawler.content.controller.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.pricescrawler.content.repository.catalog.CatalogDataRepository;
import io.github.pricescrawler.content.repository.catalog.CategoryDataRepository;
import io.github.pricescrawler.content.repository.catalog.LocaleDataRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@DirtiesContext
@AutoConfigureDataMongo
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"ACTIVE_PROFILE=demo"})
class CatalogControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

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
    void getLocales_OK() {
        var entity = restTemplate.getForEntity("/api/v1/locales", JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertFalse(entity.getBody().isEmpty());
        assertEquals(1, entity.getBody().size());
    }

    @Test
    void getLocale_OK() {
        var entity = restTemplate.getForEntity("/api/v1/locales/local", JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertFalse(entity.getBody().isEmpty());
    }

    @Test
    void getLocale_NOTFOUND() {
        var entity = restTemplate.getForEntity("/api/v1/locales/fake-locale", JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }
}
