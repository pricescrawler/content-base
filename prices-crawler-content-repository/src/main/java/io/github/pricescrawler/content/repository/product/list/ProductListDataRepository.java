package io.github.pricescrawler.content.repository.product.list;

import io.github.pricescrawler.content.common.dao.product.list.ProductListDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductListDataRepository extends MongoRepository<ProductListDao, String> {

}
