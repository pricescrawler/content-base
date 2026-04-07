package io.github.pricescrawler.content.repository.catalog;

import io.github.pricescrawler.content.common.dao.catalog.CategoryDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDataRepository extends ReactiveMongoRepository<CategoryDao, String> {

}
