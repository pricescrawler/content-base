package io.github.scafer.prices.crawler.content.service.scheduled;

import io.github.scafer.prices.crawler.content.service.product.cache.ProductCacheService;
import io.github.scafer.prices.crawler.content.service.product.list.ProductListService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class BackgroundService {
    private final ProductListService productListService;
    private final ProductCacheService productCacheService;

    public BackgroundService(ProductListService productListService, ProductCacheService productCacheService) {
        this.productListService = productListService;
        this.productCacheService = productCacheService;
    }

    @Scheduled(cron = "${prices.crawler.background.service.cron:0 0 0 * * *}")
    public void startBackgroundService() {
        log.info("Starting Background Service");
        productListService.deleteOutdatedProductLists();
        productCacheService.deleteOutdatedProductSearchResults();
    }
}
