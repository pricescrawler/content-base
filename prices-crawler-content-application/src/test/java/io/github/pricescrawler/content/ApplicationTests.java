package io.github.pricescrawler.content;

import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ApplicationTests extends BaseSpringBootTest {

    @Test
    void sanityTest() {
        var entity = restTemplate.getForEntity("/actuator/health", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void inMemoryMongoDbTest() {
        Assertions.assertThat(mongoTemplate.getDb()).isNotNull();
    }
}
