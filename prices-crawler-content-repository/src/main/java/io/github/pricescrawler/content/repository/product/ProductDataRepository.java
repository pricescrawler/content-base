package io.github.pricescrawler.content.repository.product;

import io.github.pricescrawler.content.common.dao.product.ProductDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductDataRepository extends ReactiveMongoRepository<ProductDao, String> {
    /**
     * Retrieves a list of products by ID.
     *
     * @param id the ID to search for
     * @return a list of products matching the given ID
     */
    Flux<ProductDao> findAllById(String id);

    /**
     * Retrieves a list of products by EAN/UPC.
     *
     * @param eanUpc the EAN/UPC to search for
     * @return a list of products matching the given EAN/UPC
     */
    Flux<ProductDao> findAllByEanUpcList(String eanUpc);
}
