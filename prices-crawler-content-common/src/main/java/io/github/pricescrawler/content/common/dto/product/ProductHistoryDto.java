package io.github.pricescrawler.content.common.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.pricescrawler.content.common.dao.product.ProductHistoryDao;
import io.github.pricescrawler.content.common.util.DataMapUtils;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A product with its price history. {@code prices} holds one entry per price change,
 * oldest first, each valid until the next one; when the product was seen after its last
 * change, a final entry repeats that price on {@code lastSeen}, so the history reaches
 * the last day it is known.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductHistoryDto {
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
    private String lastSeen;
    private Map<String, Object> data;
    @JsonIgnore
    private String timezone;

    public ProductHistoryDto(ProductHistoryDao product, String timezone) {
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
        this.timezone = timezone;
        this.prices = new ArrayList<>(product.getPrices() == null ? List.of() :
                product.getPrices().stream().map(value -> new PriceDto(value, timezone)).toList());
        this.lastSeen = product.getLastSeen() == null ? null : DateTimeUtils.getDateFromDateTime(product.getLastSeen(), timezone);
        this.data = DataMapUtils.getMapPublicKeys(product.getData());

        if (lastSeen != null && !prices.isEmpty() && lastSeen.compareTo(prices.getLast().getDate()) > 0) {
            prices.add(prices.getLast().withDate(lastSeen));
        }
    }

    /**
     * Keeps the prices between the two instants (inclusive, by local day). The price in
     * effect when the range starts is kept too, dated on its first day, since an entry
     * only marks when a price changed.
     */
    public ProductHistoryDto withPrices(ZonedDateTime startDate, ZonedDateTime endDate) {
        if (startDate == null || endDate == null) {
            return this;
        }

        var zone = DateTimeUtils.getZoneId(timezone);
        var start = startDate.withZoneSameInstant(zone).toLocalDate().toString();
        var end = endDate.withZoneSameInstant(zone).toLocalDate().toString();
        var inRange = new ArrayList<PriceDto>();
        PriceDto inEffect = null;

        // Local dates in ISO format sort as strings.
        for (var price : prices) {
            if (price.getDate().compareTo(start) < 0) {
                inEffect = price;
            } else if (price.getDate().compareTo(end) <= 0) {
                inRange.add(price);
            }
        }

        // Only while the product was still seen: not if it vanished before the range.
        var seenInRange = !inRange.isEmpty() || (lastSeen != null && lastSeen.compareTo(start) >= 0);

        if (inEffect != null && seenInRange && !LocalDate.parse(start).isAfter(LocalDate.parse(end))
                && (inRange.isEmpty() || !inRange.getFirst().getDate().equals(start))) {
            inRange.addFirst(inEffect.withDate(start));
        }

        this.prices = inRange;
        return this;
    }
}
