package io.github.pricescrawler.content.service.product;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByQueryDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByUrlDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductsDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface ProductService {
    /**
     * Searches for products matching the specified query.
     *
     * @param filterProductByQuery the search query to use
     * @return a Mono emitting a {@link SearchProductsDto} object containing the search results
     */
    Mono<SearchProductsDto> searchProductByQuery(FilterProductByQueryDto filterProductByQuery);

    /**
     * Searches for a product with the specified URL.
     *
     * @param filterProductByUrl the URL of the product to search for
     * @return a Mono emitting a {@link SearchProductsDto} object containing the search results
     */
    Mono<SearchProductDto> searchProductByProductUrl(FilterProductByUrlDto filterProductByUrl);

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
    List<ProductDto> parseProductsFromContent(String catalogKey, String content, String dateTime);

    /**
     * Parses a single product from the specified content.
     *
     * @param catalogKey the catalog key
     * @param query      the search query used to find the product
     * @param content    the content to parse
     * @param dateTime   the date and time to use when parsing the product
     * @return a {@link ProductDto} object representing the parsed product
     */
    ProductDto parseProductFromContent(String catalogKey, String query, String content, String dateTime);
}
