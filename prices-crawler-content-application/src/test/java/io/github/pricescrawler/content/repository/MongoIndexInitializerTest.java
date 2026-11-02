package io.github.pricescrawler.content.repository;

import io.github.pricescrawler.content.common.dao.product.ProductDao;
import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import io.github.pricescrawler.content.common.dao.product.cache.ProductCacheDao;
import io.github.pricescrawler.content.repository.config.MongoIndexInitializer;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MongoIndexInitializerTest extends BaseSpringBootTest {
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Autowired
    private MongoIndexInitializer mongoIndexInitializer;

    @Test
    void createsTheIndexesBehindTheRepositoryQueries() {
        assertTrue(indexNames(ProductHistoryDao.class).contains("eanUpcList"));
        assertTrue(indexNames(ProductDao.class).containsAll(List.of("id", "eanUpcList")));
        assertTrue(indexNames(ProductCacheDao.class).contains("products_productUrl"));
    }

    @Test
    void expiresTheSearchCache() {
        var ttl = mongoTemplate.indexOps(ProductCacheDao.class).getIndexInfo()
                .filter(index -> index.getName().equals("expiresAt"))
                .blockFirst();

        assertNotNull(ttl);
        assertEquals(Duration.ZERO, ttl.getExpireAfter().orElseThrow());
    }

    @Test
    void runningAgainIsHarmless() {
        assertDoesNotThrow(mongoIndexInitializer::createIndexes);
        assertTrue(indexNames(ProductHistoryDao.class).contains("eanUpcList"));
    }

    private List<String> indexNames(Class<?> entity) {
        return mongoTemplate.indexOps(entity).getIndexInfo().map(IndexInfo::getName).collectList().block();
    }
}
