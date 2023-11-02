package io.github.pricescrawler.content.repository.product;

import io.github.pricescrawler.content.common.dao.product.ProductDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<ProductDao, String> {
}
