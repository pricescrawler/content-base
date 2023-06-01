package io.github.pricescrawler.content.controller.product;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ProductHistoryControllerTest extends BaseSpringBootTest {
    @Test
    void shouldRetrieveProductByEanUpcSuccessfully() {
        var search = "/api/v1/products/history?eanUpc=123456789";
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertFalse(entity.getBody().isEmpty());
    }

    @Test
    void shouldReturnEmptyResultForInvalidEanUpc() {
        var search = "/api/v1/products/history?eanUpc=0";
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue(entity.getBody().isEmpty());
    }

    @Test
    void shouldRetrieveProductByLocaleCatalogAndReferenceSuccessfully() {
        var search = "/api/v1/products/history/local/demo/1";
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForInvalidLocaleCatalog() {
        var search = "/api/v1/products/history/local/dummy/1";
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }
}