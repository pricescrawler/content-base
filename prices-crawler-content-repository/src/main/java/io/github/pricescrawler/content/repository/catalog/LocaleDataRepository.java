package io.github.pricescrawler.content.repository.catalog;

import io.github.pricescrawler.content.common.dao.catalog.LocaleDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocaleDataRepository extends ReactiveMongoRepository<LocaleDao, String> {

}
