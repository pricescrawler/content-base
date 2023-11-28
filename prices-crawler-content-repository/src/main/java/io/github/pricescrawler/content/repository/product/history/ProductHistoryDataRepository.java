package io.github.pricescrawler.content.repository.product.history;

import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductHistoryDataRepository extends MongoRepository<ProductHistoryDao, String> {
    /**
     * Retrieves a list of products by EAN/UPC.
     *
     * @param eanUpc the EAN/UPC to search for
     * @return a list of products matching the given EAN/UPC
     */
    List<ProductHistoryDao> findAllByEanUpcList(String eanUpc);
}
