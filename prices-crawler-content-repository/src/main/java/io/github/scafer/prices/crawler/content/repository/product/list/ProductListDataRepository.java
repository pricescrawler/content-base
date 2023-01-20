package io.github.scafer.prices.crawler.content.repository.product.list;

import io.github.scafer.prices.crawler.content.common.dao.product.list.ProductListDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductListDataRepository extends MongoRepository<ProductListDao, String> {
}
