package io.github.scafer.prices.crawler.content.service.product;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
import io.github.scafer.prices.crawler.content.common.dto.product.ProductListItemDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductDto;
import io.github.scafer.prices.crawler.content.common.dto.product.search.SearchProductsDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface ProductService {
    /**
     * Searches for products matching the specified query.
     *
     * @param query the search query to use
     * @return a Mono emitting a {@link SearchProductsDto} object containing the search results
     */
    Mono<SearchProductsDto> searchProductByQuery(String query);

    /**
     * Searches for a product with the specified URL.
     *
     * @param productUrl the URL of the product to search for
     * @return a Mono emitting a {@link SearchProductsDto} object containing the search results
     */
    Mono<SearchProductDto> searchProductByProductUrl(String productUrl);

    /**
     * Updates the provided {@link ProductListItemDto} object.
     *
     * @param productListItem the {@link ProductListItemDto} object to update
     * @return a Mono emitting the updated {@link ProductListItemDto} object
     */
    Mono<ProductListItemDto> updateProductListItem(ProductListItemDto productListItem);

    /**
     * Parses a list of products from the specified content.
     *
     * @param content  the content to parse
     * @param dateTime the date and time to use when parsing the products
     * @return a list of {@link ProductDto} objects representing the parsed products
     */
    List<ProductDto> parseProductsFromContent(String content, String dateTime);

    /**
     * Parses a single product from the specified content.
     *
     * @param content  the content to parse
     * @param query    the search query used to find the product
     * @param dateTime the date and time to use when parsing the product
     * @return a {@link ProductDto} object representing the parsed product
     */
    ProductDto parseProductFromContent(String content, String query, String dateTime);
}
