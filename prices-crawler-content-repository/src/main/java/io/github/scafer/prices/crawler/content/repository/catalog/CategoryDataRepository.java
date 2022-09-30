package io.github.scafer.prices.crawler.content.repository.catalog;

import io.github.scafer.prices.crawler.content.common.dao.catalog.CategoryDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDataRepository extends MongoRepository<CategoryDao, String> {

}
