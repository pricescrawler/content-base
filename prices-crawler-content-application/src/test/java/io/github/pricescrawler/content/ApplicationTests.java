package io.github.pricescrawler.content;

import io.github.pricescrawler.content.util.MongoContainerTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"ACTIVE_PROFILE=demo"})
class ApplicationTests extends MongoContainerTest {
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private TestRestTemplate restTemplate;

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
