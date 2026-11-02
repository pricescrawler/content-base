package io.github.pricescrawler.content.repository.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "prices.crawler.product.data")
public class ProductDataConfig {
    private boolean hintsEnabled = true;
    private boolean searchTermsEnabled = true;
    /**
     * Most recent search terms kept per product; 0 keeps them all.
     */
    private int searchTermsLimit = 50;
}
