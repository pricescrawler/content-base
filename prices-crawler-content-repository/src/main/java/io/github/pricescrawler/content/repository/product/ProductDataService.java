package io.github.pricescrawler.content.repository.product;

import io.github.pricescrawler.content.common.dao.product.ProductDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;

import java.util.List;

public interface ProductDataService {

    /**
     * Retrieves a list of products based on the specified criteria.
     *
     * @param locale    The locale for which products are to be retrieved.
     * @param catalog   The catalog identifier for filtering products.
     * @param reference The reference identifier for further filtering products.
     * @return A list of ProductDao objects matching the specified criteria.
     */
    List<ProductDao> findProducts(String locale, String catalog, String reference);

    /**
     * Retrieves a list of products based on the provided EAN/UPC code.
     *
     * @param eanUpc The EAN (European Article Number) or UPC (Universal Product Code) for identifying products.
     * @return A list of ProductDao objects matching the specified EAN/UPC code.
     */
    List<ProductDao> findProductsByEanUpc(String eanUpc);

    /**
     * Saves a list of product data transfer objects (DTOs) to the underlying data storage.
     *
     * @param productDtoList The list of ProductDto objects to be saved.
     */
    void save(List<ProductDto> productDtoList);
}

