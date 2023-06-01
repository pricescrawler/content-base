package io.github.pricescrawler.content.controller.product;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.pricescrawler.content.common.dto.product.search.SearchQueryDto;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductSearchControllerTest extends BaseSpringBootTest {
    @Test
    void shouldRetrieveProductsByQuerySuccessfully() {
        var search = SearchQueryDto.builder().query("query").catalogs(new String[]{"local.demo"}).build();
        var entity = restTemplate.postForEntity("/api/v1/products/search", search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForInvalidQuery() {
        var search = SearchQueryDto.builder().query("query").catalogs(new String[]{"local.fake-catalog"}).build();
        var entity = restTemplate.postForEntity("/api/v1/products/search", search, JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    void shouldRetrieveProductByUrlSuccessfully() {
        var search = String.format("/api/v1/products/search/%s/%s/%s", "local", "demo", "url.local");
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForInvalidUrl() {
        var search = String.format("/api/v1/products/search/%s/%s/%s", "local", "dummy", "url.local");
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }
}

