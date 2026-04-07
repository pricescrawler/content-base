package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductHistoryControllerTest extends BaseSpringBootTest {
    @Test
    void shouldRetrieveProductByEanUpcSuccessfully() {
        webTestClient.get().uri("/api/v1/products/history?eanUpc=123456789")
                .exchange()
                .expectStatus().isOk()
                .expectBody(JsonNode.class)
                .value(body -> assertFalse(body.isEmpty()));
    }

    @Test
    void shouldReturnEmptyResultForInvalidEanUpc() {
        webTestClient.get().uri("/api/v1/products/history?eanUpc=0")
                .exchange()
                .expectStatus().isOk()
                .expectBody(JsonNode.class)
                .value(body -> assertTrue(body.isEmpty()));
    }

    @Test
    void shouldRetrieveProductByLocaleCatalogAndReferenceSuccessfully() {
        webTestClient.get().uri("/api/v1/products/history/local/demo/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnNotFoundForInvalidLocaleCatalog() {
        webTestClient.get().uri("/api/v1/products/history/local/dummy/1")
                .exchange()
                .expectStatus().isNotFound();
    }
}
