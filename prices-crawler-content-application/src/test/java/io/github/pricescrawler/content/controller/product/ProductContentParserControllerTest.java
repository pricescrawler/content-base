package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dto.product.parser.ProductContentDto;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ProductContentParserControllerTest extends BaseSpringBootTest {
    @Test
    void shouldParseSingleProductFromContentSuccessfully() {
        webTestClient.post().uri("/api/v1/products/parser")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProductContentDto.builder().catalog("local.demo").build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldParseProductListFromContentSuccessfully() {
        webTestClient.post().uri("/api/v1/products/parser/list")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProductContentDto.builder().catalog("local.demo").build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnNotFoundForInvalidSingleProductCatalog() {
        webTestClient.post().uri("/api/v1/products/parser")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProductContentDto.builder().catalog("dummy.dummy").build())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturnNotFoundForInvalidProductListCatalog() {
        webTestClient.post().uri("/api/v1/products/parser/list")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProductContentDto.builder().catalog("dummy.dummy").build())
                .exchange()
                .expectStatus().isNotFound();
    }
}
