package io.github.pricescrawler.content.common.dao.product.incident;

import io.github.pricescrawler.content.common.dao.base.Identifiable;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document("product-incident")
@EqualsAndHashCode(callSuper = true)
public class ProductIncidentDao extends Identifiable {
    private List<ProductDto> products;
    private String description;
    @Builder.Default
    private boolean isMerged = false;
    @Builder.Default
    private boolean isClosed = false;
    @Builder.Default
    private int hits = 1;
    private List<String> searchTerms;

    public ProductIncidentDao closed() {
        this.isClosed = true;
        return this;
    }

    public ProductIncidentDao merged() {
        this.isMerged = true;
        return this;
    }

    public void incrementHits() {
        this.hits++;
    }

    public void addProduct(ProductDto product) {
        this.products.add(product);
    }
}
