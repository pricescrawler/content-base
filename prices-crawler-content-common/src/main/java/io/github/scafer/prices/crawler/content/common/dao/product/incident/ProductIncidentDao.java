package io.github.scafer.prices.crawler.content.common.dao.product.incident;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
import io.github.scafer.prices.crawler.content.common.util.DateTimeUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Document("product-incidents")
public class ProductIncidentDao {
    @Id
    private String id;
    private List<ProductDto> products;
    private String description;
    private boolean merged = false;
    private boolean closed = false;
    private int hits = 1;
    private List<String> searchTerms;
    private String created;
    private String updated;
    private Map<String, Object> data;

    public ProductIncidentDao(String productId, ProductDto product) {
        this.id = productId;
        this.products = List.of(product);
        this.created = DateTimeUtils.getCurrentDateTime();
        this.updated = DateTimeUtils.getCurrentDateTime();
    }

    public ProductIncidentDao closed() {
        this.closed = true;
        return this;
    }

    public ProductIncidentDao merged() {
        this.merged = true;
        return this;
    }

    public void incrementHits() {
        this.hits++;
    }

    public void addNewProduct(ProductDto newProduct) {
        this.products.add(newProduct);
    }
}
