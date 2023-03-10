package io.github.pricescrawler.content.repository.product.cache;

import io.github.pricescrawler.content.common.dao.product.cache.ProductCacheDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCacheDataRepository extends MongoRepository<ProductCacheDao, String> {

}
