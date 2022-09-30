package io.github.scafer.prices.crawler.content.common.dto.product;

import io.github.scafer.prices.crawler.content.common.dao.product.PriceDao;
import io.github.scafer.prices.crawler.content.common.util.DataMapUtils;
import io.github.scafer.prices.crawler.content.common.util.DateTimeUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
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
        this.date = DateTimeUtils.extractDate(price.getDate());
        this.data = DataMapUtils.getMapPublicKeys(price.getData());
    }
}
