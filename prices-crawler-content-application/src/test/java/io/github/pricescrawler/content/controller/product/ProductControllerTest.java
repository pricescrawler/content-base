package io.github.pricescrawler.content.controller.product;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductControllerTest extends BaseSpringBootTest {

    @Test
    void shouldIngestProductsSuccessfully() {
        var body = List.of(ProductDto.builder().build());
        var entity = restTemplate.postForEntity("/api/v1/products/ingest", body, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }
}
