package io.github.scafer.prices.crawler.content.repository.product.incident;

import io.github.scafer.prices.crawler.content.common.dao.product.incident.ProductIncidentDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductIncidentDataRepository extends MongoRepository<ProductIncidentDao, String> {

}
