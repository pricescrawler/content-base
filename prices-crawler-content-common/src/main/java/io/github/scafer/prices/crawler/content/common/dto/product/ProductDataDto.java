package io.github.scafer.prices.crawler.content.common.dto.product;

import io.github.scafer.prices.crawler.content.common.dao.product.ProductDao;
import io.github.scafer.prices.crawler.content.common.util.DataMapUtils;
import io.github.scafer.prices.crawler.content.common.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDataDto {
    private String locale;
    private String catalog;
    private String reference;
    private String name;
    private String quantity;
    private String brand;
    private String description;
    private String productUrl;
    private String imageUrl;
    private List<String> eanUpc;
    private List<String> searchTerms;
    private List<PriceDto> prices;
    private Map<String, Object> data;

    public ProductDataDto(ProductDao product) {
        this.locale = product.getLocale();
        this.catalog = product.getCatalog();
        this.reference = product.getReference();
        this.name = product.getName();
        this.quantity = product.getQuantity();
        this.brand = product.getBrand();
        this.description = product.getDescription();
        this.productUrl = product.getProductUrl();
        this.imageUrl = product.getImageUrl();
        this.eanUpc = product.getEanUpcList();
        this.prices = product.getPrices().stream().map(PriceDto::new).toList();
        this.data = DataMapUtils.getMapPublicKeys(product.getData());
    }

    public ProductDataDto withPrices(ZonedDateTime startDate, ZonedDateTime endDate) {
        if (startDate == null || endDate == null) {
            return this;
        }

        this.getPrices().removeIf(price -> !DateTimeUtils.isBetweenDates(price.getDate(), startDate, endDate));

        return this;
    }
}
