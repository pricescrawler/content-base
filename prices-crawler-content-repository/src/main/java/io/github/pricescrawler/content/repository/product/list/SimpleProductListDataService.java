package io.github.pricescrawler.content.repository.product.list;

import io.github.pricescrawler.content.common.dao.product.list.ProductListDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SimpleProductListDataService implements ProductListDataService {
    private final ProductListDataRepository productListDataRepository;

    public SimpleProductListDataService(ProductListDataRepository productListDataRepository) {
        this.productListDataRepository = productListDataRepository;
    }

    @Override
    public List<ProductListDao> findAllProductList() {
        return productListDataRepository.findAll();
    }

    @Override
    public Optional<ProductListDao> findProductListById(String id) {
        return productListDataRepository.findById(id);
    }

    @Override
    public ProductListDao saveProductList(ProductListDao productList) {
        return productListDataRepository.save(productList);
    }

    @Override
    public void deleteProductList(String id) {
        productListDataRepository.deleteById(id);
    }
}
