package io.github.pricescrawler.content.repository.config;

import io.github.pricescrawler.content.common.dao.product.ProductDao;
import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import io.github.pricescrawler.content.common.dao.product.cache.ProductCacheDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Creates the indexes behind the repository queries. Spring Data's automatic index
 * creation is off by default, so {@code @Indexed}/{@code @CompoundIndex} annotations are
 * never applied; without these, every lookup by EAN/UPC or product URL scans the whole
 * collection. Also adds the TTL index that expires the search cache. Creating an index that already exists is a no-op, so this runs on every
 * start. A failure is logged and does not stop the application.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "prices.crawler.mongodb.indexes.enabled", matchIfMissing = true)
public class MongoIndexInitializer {
    private static final Duration TIMEOUT = Duration.ofSeconds(60);
    private static final String EXPIRES_AT = "expiresAt";

    private final ReactiveMongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void createIndexes() {
        try {
            Flux.concat(
                            // "Available in other stores" and the MCP barcode lookup.
                            create(ProductHistoryDao.class, "eanUpcList"),
                            // Individual (non-aggregated) history lookups.
                            create(ProductDao.class, "id"),
                            create(ProductDao.class, "eanUpcList"),
                            // Cache lookups by product URL.
                            create(ProductCacheDao.class, "products.productUrl"),
                            // MongoDB deletes cache entries once they expire.
                            mongoTemplate.indexOps(ProductCacheDao.class).createIndex(new Index()
                                    .on(EXPIRES_AT, Sort.Direction.ASC).named(EXPIRES_AT).expire(Duration.ZERO)))
                    .doOnNext(name -> log.info("MongoDB index ready: {}", name))
                    .then()
                    .block(TIMEOUT);
        } catch (Exception exception) {
            log.error("Could not create MongoDB indexes", exception);
        }
    }

    private Mono<String> create(Class<?> entity, String field) {
        return mongoTemplate.indexOps(entity)
                .createIndex(new Index().on(field, Sort.Direction.ASC).named(field.replace('.', '_')));
    }
}
