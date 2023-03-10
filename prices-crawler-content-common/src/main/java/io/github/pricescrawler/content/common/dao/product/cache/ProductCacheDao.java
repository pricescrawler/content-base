package io.github.pricescrawler.content.common.dao.product.cache;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.cache.ProductCacheDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("product-cache")
@EqualsAndHashCode(callSuper = true)
public class ProductCacheDao extends ProductCacheDto {
    @Id
    private String id;

    public ProductCacheDao(String id, String currentDateTime, List<ProductDto> products) {
        super(currentDateTime, products);
        this.id = id;
    }
}
