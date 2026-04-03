package io.github.pricescrawler.content.controller.catalog;

import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.assertFalse;

class CatalogControllerTest extends BaseSpringBootTest {
    @Test
    void shouldGetLocalesSuccessfully() {
        webTestClient.get().uri("/api/v1/locales")
                .exchange()
                .expectStatus().isOk()
                .expectBody(JsonNode.class)
                .value(body -> assertFalse(body.isEmpty()));
    }

    @Test
    void shouldGetLocaleSuccessfully() {
        webTestClient.get().uri("/api/v1/locales/local")
                .exchange()
                .expectStatus().isOk()
                .expectBody(JsonNode.class)
                .value(body -> assertFalse(body.isEmpty()));
    }

    @Test
    void shouldReturnNotFoundForInvalidLocale() {
        webTestClient.get().uri("/api/v1/locales/fake-locale")
                .exchange()
                .expectStatus().isNotFound();
    }
}
