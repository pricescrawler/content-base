package io.github.pricescrawler.content.repository.product.incident;

import io.github.pricescrawler.content.common.dao.product.incident.ProductIncidentDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductIncidentDataRepository extends MongoRepository<ProductIncidentDao, String> {

}
