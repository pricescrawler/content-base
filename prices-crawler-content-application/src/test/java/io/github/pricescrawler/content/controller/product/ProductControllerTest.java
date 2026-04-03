package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

class ProductControllerTest extends BaseSpringBootTest {
    @Test
    void shouldIngestProductsSuccessfully() {
        webTestClient.post().uri("/api/v1/products/ingest")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(ProductDto.builder().build()))
                .exchange()
                .expectStatus().isOk();
    }
}
