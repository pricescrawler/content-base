package io.github.pricescrawler.content.repository.product.list;

import io.github.pricescrawler.content.common.dao.product.list.ProductListDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SimpleProductListDataService implements ProductListDataService {
    private final ProductListDataRepository productListDataRepository;

    @Override
    public Flux<ProductListDao> findAllProductList() {
        return productListDataRepository.findAll();
    }

    @Override
    public Mono<ProductListDao> findProductListById(String id) {
        return productListDataRepository.findById(id);
    }

    @Override
    public Mono<ProductListDao> saveProductList(ProductListDao productList) {
        return productListDataRepository.save(productList);
    }

    @Override
    public Mono<Void> deleteProductList(String id) {
        return productListDataRepository.deleteById(id);
    }
}
