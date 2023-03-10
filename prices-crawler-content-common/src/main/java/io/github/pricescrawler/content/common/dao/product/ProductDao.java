package io.github.pricescrawler.content.common.dao.product;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.common.util.IdUtils;
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
@Document("products")
public class ProductDao {
    @Id
    private String id;
    private String locale;
    private String catalog;
    private String reference;
    private String name;
    private String brand;
    private String quantity;
    private String description;
    private String imageUrl;
    private String productUrl;
    private List<String> eanUpcList;
    private Boolean isActive = true;
    private int hits;
    private String created;
    private String updated;
    private List<String> searchTerms;
    private List<PriceDao> prices;
    private Map<String, Object> data;

    public ProductDao(String locale, String catalog, ProductDto product) {
        this.locale = locale;
        this.catalog = catalog;
        this.created = DateTimeUtils.getCurrentDateTime();
        updateFromProduct(product).incrementHits();
    }

    public ProductDao updateFromProduct(ProductDto product) {
        this.id = IdUtils.parse(getLocale(), getCatalog(), product.getReference());
        this.reference = product.getReference();
        this.name = product.getName();
        this.brand = product.getBrand();
        this.quantity = product.getQuantity();
        this.description = product.getDescription();
        this.imageUrl = product.getImageUrl();
        this.productUrl = product.getProductUrl();
        this.updated = DateTimeUtils.getCurrentDateTime();
        this.data = product.getData();
        return this;
    }

    public void incrementHits() {
        this.hits++;
    }

    public void incrementHits(int numberOfHits) {
        this.hits += numberOfHits;
    }
}
