package io.github.pricescrawler.content.repository.feedback;

import io.github.pricescrawler.content.common.dao.feedback.FeedbackDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackDataRepository extends ReactiveMongoRepository<FeedbackDao, String> {

}
