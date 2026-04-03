package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dao.product.list.ProductListDao;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductDto;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

class ProductListControllerTest extends BaseSpringBootTest {
    @Test
    void shouldUpdateProductListSuccessfully() {
        var body = List.of(SearchProductDto.builder()
                .locale("local")
                .catalog("demo")
                .product(createProductDto())
                .build());

        webTestClient.post().uri("/api/v1/products/list/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnNotFoundForInvalidUpdateProductListQuery() {
        webTestClient.post().uri("/api/v1/products/list/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnNotFoundForInvalidStoreProductList() {
        var body = List.of(SearchProductDto.builder()
                .locale("dummy")
                .catalog("dummy")
                .product(createProductDto())
                .build());

        webTestClient.post().uri("/api/v1/products/list/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldStoreProductListSuccessfully() {
        var body = List.of(SearchProductDto.builder()
                .locale("local")
                .catalog("demo")
                .product(createProductDto())
                .build());

        webTestClient.post().uri("/api/v1/products/list/store")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnNotFoundForInvalidStoreProductListQuery() {
        webTestClient.post().uri("/api/v1/products/list/store")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnProductListSuccessfully() {
        var list = productListDataRepository.save(ProductListDao.builder().items(List.of()).build()).block();
        webTestClient.get().uri("/api/v1/products/list?id=" + list.getId())
                .exchange()
                .expectStatus().isOk();
    }
}
