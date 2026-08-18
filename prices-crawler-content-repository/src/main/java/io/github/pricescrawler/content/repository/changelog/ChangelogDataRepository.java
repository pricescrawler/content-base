package io.github.pricescrawler.content.repository.changelog;

import io.github.pricescrawler.content.common.dao.changelog.ChangelogDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChangelogDataRepository extends ReactiveMongoRepository<ChangelogDao, String> {

    Flux<ChangelogDao> findAllByOrderByDateDesc();
}
