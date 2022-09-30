package io.github.scafer.prices.crawler.content.common.dao.product;

import io.github.scafer.prices.crawler.content.common.dto.product.ProductDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
public class PriceDao {
    private String regularPrice;
    private String campaignPrice;
    private String pricePerQuantity;
    private String quantity;
    private String date;
    private Map<String, Object> data;

    public PriceDao(ProductDto productDto) {
        this.regularPrice = productDto.getRegularPrice();
        this.campaignPrice = productDto.getCampaignPrice();
        this.pricePerQuantity = productDto.getPricePerQuantity();
        this.quantity = productDto.getQuantity() == null || productDto.getQuantity().isEmpty() ? productDto.getName() : productDto.getQuantity();
        this.date = productDto.getDate();
        this.data = productDto.getData();
    }

    public String getDate() {
        try {
            return this.date;
        } catch (Exception exception) {
            return String.valueOf(ZonedDateTime.parse(new SimpleDateFormat("yyy/MM/dd HH:mm:ss").format(this.date)));
        }
    }

    public void setDate(String date) {
        try {
            this.date = date;
        } catch (Exception exception) {
            this.date = String.valueOf(ZonedDateTime.parse(new SimpleDateFormat("yyy/MM/dd HH:mm:ss").format(date)));
        }
    }
}
