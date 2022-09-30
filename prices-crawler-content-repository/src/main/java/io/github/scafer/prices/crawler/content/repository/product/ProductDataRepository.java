package io.github.scafer.prices.crawler.content.repository.product;

import io.github.scafer.prices.crawler.content.common.dao.product.ProductDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDataRepository extends MongoRepository<ProductDao, String> {

}
