package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dto.product.search.SearchQueryDto;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ProductSearchControllerTest extends BaseSpringBootTest {
    @Test
    void shouldRetrieveProductsByQuerySuccessfully() {
        var body = SearchQueryDto.builder().query("query").catalogs(new String[]{"local.demo"}).build();
        webTestClient.post().uri("/api/v1/products/search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnNotFoundForInvalidQuery() {
        var body = SearchQueryDto.builder().query("query").catalogs(new String[]{"local.fake-catalog"}).build();
        webTestClient.post().uri("/api/v1/products/search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldRetrieveProductByUrlSuccessfully() {
        webTestClient.get().uri("/api/v1/products/search/local/demo/url.local")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnNotFoundForInvalidUrl() {
        webTestClient.get().uri("/api/v1/products/search/local/dummy/url.local")
                .exchange()
                .expectStatus().isNotFound();
    }
}
