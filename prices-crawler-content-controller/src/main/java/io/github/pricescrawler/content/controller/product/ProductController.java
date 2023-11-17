package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.repository.product.ProductDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/ingest")
@ConditionalOnProperty("prices.crawler.controller.product.enabled")
public class ProductController {
    private final ProductDataService productDataService;

    @PostMapping
    public void ingestProduct(@RequestBody List<ProductDto> productDtoList) {
        productDataService.save(productDtoList);
    }
}
