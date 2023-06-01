package io.github.pricescrawler.content.controller.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CatalogControllerTest extends BaseSpringBootTest {
    @Test
    void shouldGetLocalesSuccessfully() {
        var entity = restTemplate.getForEntity("/api/v1/locales", JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertFalse(entity.getBody().isEmpty());
    }

    @Test
    void shouldGetLocaleSuccessfully() {
        var entity = restTemplate.getForEntity("/api/v1/locales/local", JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertFalse(entity.getBody().isEmpty());
    }

    @Test
    void shouldReturnNotFoundForInvalidLocale() {
        var entity = restTemplate.getForEntity("/api/v1/locales/fake-locale", JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }
}

