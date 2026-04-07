package io.github.pricescrawler.content.repository.product.history;

import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductHistoryDataRepository extends ReactiveMongoRepository<ProductHistoryDao, String> {
    /**
     * Retrieves a list of products by EAN/UPC.
     *
     * @param eanUpc the EAN/UPC to search for
     * @return a list of products matching the given EAN/UPC
     */
    Flux<ProductHistoryDao> findAllByEanUpcList(String eanUpc);
}
