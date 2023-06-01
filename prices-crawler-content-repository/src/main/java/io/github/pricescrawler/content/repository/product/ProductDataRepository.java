package io.github.pricescrawler.content.repository.product;

import io.github.pricescrawler.content.common.dao.product.ProductDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDataRepository extends MongoRepository<ProductDao, String> {
    /**
     * Retrieves a list of products by EAN/UPC.
     *
     * @param eanUpc the EAN/UPC to search for
     * @return a list of products matching the given EAN/UPC
     */
    List<ProductDao> findAllByEanUpcList(String eanUpc);
}
