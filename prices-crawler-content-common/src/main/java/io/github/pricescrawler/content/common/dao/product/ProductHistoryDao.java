package io.github.pricescrawler.content.common.dao.product;

import io.github.pricescrawler.content.common.dao.base.Identifiable;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.common.util.IdUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document("product-history")
@EqualsAndHashCode(callSuper = true)
public class ProductHistoryDao extends Identifiable {
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
    @Builder.Default
    private Boolean isActive = true;
    private int hits;
    private List<String> searchTerms;
    private List<PriceDao> prices;

    public ProductHistoryDao(String locale, String catalog, ProductDto product) {
        this.locale = locale;
        this.catalog = catalog;
        setCreated(DateTimeUtils.getCurrentDateTime());
        updateFromProduct(product).incrementHits();
    }

    public ProductHistoryDao updateFromProduct(ProductDto product) {
        setId(IdUtils.parse(getLocale(), getCatalog(), product.getReference()));
        setUpdated(DateTimeUtils.getCurrentDateTime());
        setData(product.getData());
        this.reference = product.getReference();
        this.name = product.getName();
        this.brand = product.getBrand();
        this.quantity = product.getQuantity();
        this.description = product.getDescription();
        this.imageUrl = product.getImageUrl();
        this.productUrl = product.getProductUrl();
        return this;
    }

    public void incrementHits() {
        this.hits++;
    }

    public void incrementHits(int numberOfHits) {
        this.hits += numberOfHits;
    }
}
