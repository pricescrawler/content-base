package io.github.scafer.prices.crawler.content.service.product;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductListItemDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductsDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface ProductService {
    Mono<SearchProductsDto> searchProduct(String query);

    Mono<SearchProductDto> searchProductByUrl(String productUrl);

    Mono<ProductListItemDto> updateProduct(ProductListItemDto productListItem);
}
