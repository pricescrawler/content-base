package io.github.pricescrawler.content.controller.product;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.pricescrawler.content.common.dao.product.list.ProductListDao;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductDto;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductListControllerTest extends BaseSpringBootTest {
    @Test
    void shouldUpdateProductListSuccessfully() {
        var updateProducts = SearchProductDto.builder()
                .locale("local")
                .catalog("demo")
                .product(createProductDto())
                .build();

        var entity = restTemplate.postForEntity("/api/v1/products/list/update", List.of(updateProducts), JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForInvalidUpdateProductListQuery() {
        var entity = restTemplate.postForEntity("/api/v1/products/list/update", List.of(), JsonNode.class);
        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForInvalidStoreProductList() {
        var updateProducts = SearchProductDto.builder()
                .locale("dummy")
                .catalog("dummy")
                .product(createProductDto())
                .build();

        var entity = restTemplate.postForEntity("/api/v1/products/list/update", List.of(updateProducts), JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    void shouldStoreProductListSuccessfully() {
        var updateProducts = SearchProductDto.builder()
                .locale("local")
                .catalog("demo")
                .product(createProductDto())
                .build();

        var entity = restTemplate.postForEntity("/api/v1/products/list/store", List.of(updateProducts), JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForInvalidStoreProductListQuery() {
        var entity = restTemplate.postForEntity("/api/v1/products/list/store", List.of(), JsonNode.class);
        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }

    @Test
    void shouldReturnProductListSuccessfully() {
        var list = productListDataRepository.save(ProductListDao.builder().build());
        var entity = restTemplate.getForEntity(String.format("/api/v1/products/list?id=%s", list.getId()), JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }
}

