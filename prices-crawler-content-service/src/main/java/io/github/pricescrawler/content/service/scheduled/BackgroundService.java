package io.github.pricescrawler.content.service.scheduled;

import io.github.pricescrawler.content.service.product.cache.ProductCacheService;
import io.github.pricescrawler.content.service.product.list.ProductListService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class BackgroundService {
    private final ProductListService productListService;
    private final ProductCacheService localProductCacheService;
    private final ProductCacheService mongoDbProductCacheService;

    public BackgroundService(ProductListService productListService,
                             @Qualifier("inMemoryProductCacheService")
                             ProductCacheService localProductCacheService,
                             @Qualifier("mongoDbProductCacheService")
                             ProductCacheService mongoDbProductCacheService) {
        this.productListService = productListService;
        this.localProductCacheService = localProductCacheService;
        this.mongoDbProductCacheService = mongoDbProductCacheService;
    }

    @Scheduled(cron = "${prices.crawler.background.service.cron:0 0 0 * * *}")
    public void startBackgroundService() {
        log.info("Starting Background Service");
        productListService.deleteOutdatedProductLists();
        localProductCacheService.deleteOutdatedProductSearchResults();
        mongoDbProductCacheService.deleteOutdatedProductSearchResults();
    }
}
