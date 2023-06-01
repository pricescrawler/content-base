package io.github.pricescrawler.content.controller.product;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.pricescrawler.content.common.dto.product.parser.RawProductContentDto;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductContentParserControllerTest extends BaseSpringBootTest {
    @Test
    void shouldParseSingleProductFromContentSuccessfully() {
        var body = RawProductContentDto.builder().catalog("local.demo").build();
        var entity = restTemplate.postForEntity("/api/v1/products/parser", body, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void shouldParseProductListFromContentSuccessfully() {
        var body = RawProductContentDto.builder().catalog("local.demo").build();
        var entity = restTemplate.postForEntity("/api/v1/products/parser/list", body, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForInvalidSingleProductCatalog() {
        var body = RawProductContentDto.builder().catalog("dummy.dummy").build();
        var entity = restTemplate.postForEntity("/api/v1/products/parser", body, JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForInvalidProductListCatalog() {
        var body = RawProductContentDto.builder().catalog("dummy.dummy").build();
        var entity = restTemplate.postForEntity("/api/v1/products/parser/list", body, JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }
}

