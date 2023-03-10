package io.github.pricescrawler.content.common.dto.product;

import io.github.pricescrawler.content.common.dao.product.PriceDao;
import io.github.pricescrawler.content.common.util.DataMapUtils;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {
    private String regularPrice;
    private String campaignPrice;
    private String pricePerQuantity;
    private String quantity;
    private String date;
    private Map<String, Object> data;

    public PriceDto(PriceDao price) {
        this.regularPrice = price.getRegularPrice();
        this.campaignPrice = price.getCampaignPrice();
        this.pricePerQuantity = price.getPricePerQuantity();
        this.quantity = price.getQuantity();
        this.date = DateTimeUtils.getDateFromDateTime(price.getDate());
        this.data = DataMapUtils.getMapPublicKeys(price.getData());
    }
}
