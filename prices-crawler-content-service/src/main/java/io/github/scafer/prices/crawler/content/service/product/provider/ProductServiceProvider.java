package io.github.scafer.prices.crawler.content.service.product.provider;

import io.github.scafer.prices.crawler.content.common.provider.GenericServiceProvider;
import io.github.scafer.prices.crawler.content.service.product.ProductService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceProvider extends GenericServiceProvider<ProductService> {
    public ProductServiceProvider(ApplicationContext appContext) {
        super(appContext, ProductService.class);
    }
}
