package io.github.pricescrawler.content.common.dao.product.incident;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("product-incident")
public class ProductIncidentDao {
    @Id
    private String id;
    private List<ProductDto> products;
    private String description;
    @Builder.Default
    private boolean isMerged = false;
    @Builder.Default
    private boolean isClosed = false;
    @Builder.Default
    private int hits = 1;
    private List<String> searchTerms;
    private String created;
    private String updated;
    private Map<String, Object> data;

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
