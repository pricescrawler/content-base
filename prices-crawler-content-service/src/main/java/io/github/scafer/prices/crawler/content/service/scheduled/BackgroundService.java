package io.github.scafer.prices.crawler.content.service.scheduled;

import io.github.scafer.prices.crawler.content.service.product.cache.ProductCacheService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class BackgroundService {
    private final ProductCacheService productCacheService;

    public BackgroundService(ProductCacheService productCacheService) {
        this.productCacheService = productCacheService;
    }

    @Scheduled(fixedRateString = "${prices.crawler.background.service.refreshInterval:3600000}")
    public void startBackgroundService() {
        log.info("Starting Background Service");
        productCacheService.clearOutdatedProducts();
    }
}
