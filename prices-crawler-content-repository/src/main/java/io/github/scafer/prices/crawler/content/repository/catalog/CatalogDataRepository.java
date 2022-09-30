package io.github.scafer.prices.crawler.content.repository.catalog;

import io.github.scafer.prices.crawler.content.common.dao.catalog.CatalogDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogDataRepository extends MongoRepository<CatalogDao, String> {
    List<CatalogDao> findAllByLocalesContains(String locale);

    List<CatalogDao> findAllByLocalesContainsAndCategoriesContains(String locale, String category);
}
