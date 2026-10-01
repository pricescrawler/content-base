package io.github.pricescrawler.content.repository.product.cache;

import io.github.pricescrawler.content.common.dao.product.cache.ProductCacheDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductCacheDataRepository extends ReactiveMongoRepository<ProductCacheDao, String> {

    /**
     * Finds the cached search results that contain a product with the given URL, so
     * lookups by URL no longer scan the whole collection.
     */
    Flux<ProductCacheDao> findAllByProductsProductUrl(String productUrl);
}
