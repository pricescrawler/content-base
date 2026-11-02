package io.github.pricescrawler.content.common.dao.product.cache;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.cache.ProductCacheDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document("product-cache")
@CompoundIndex(name = "products_productUrl", def = "{'products.productUrl': 1}")
@EqualsAndHashCode(callSuper = true)
public class ProductCacheDao extends ProductCacheDto {
    @Id
    private String id;
    /**
     * When MongoDB may delete the entry (TTL index); the end of the day it was cached on.
     */
    private Instant expiresAt;

    public ProductCacheDao(String id, String currentDateTime, List<ProductDto> products) {
        super(currentDateTime, products);
        this.id = id;
    }

    public ProductCacheDao(String id, String currentDateTime, List<ProductDto> products, Instant expiresAt) {
        this(id, currentDateTime, products);
        this.expiresAt = expiresAt;
    }
}
