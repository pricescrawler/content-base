package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dao.product.ProductDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.repository.product.ProductRepository;
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
    private final ProductRepository productRepository;

    @PostMapping
    public void ingestProduct(@RequestBody List<ProductDto> productDto) {
        productRepository.saveAll(
                productDto.stream().map(value ->
                        ProductDao.builder()
                                .id(value.getId())
                                .productUrl(value.getProductUrl())
                                .brand(value.getBrand())
                                .campaignPrice(value.getCampaignPrice())
                                .imageUrl(value.getImageUrl())
                                .name(value.getName())
                                .date(value.getDate())
                                .description(value.getDescription())
                                .reference(value.getReference())
                                .regularPrice(value.getRegularPrice())
                                .eanUpcList(value.getEanUpcList())
                                .pricePerQuantity(value.getPricePerQuantity())
                                .quantity(value.getQuantity())
                                .data(value.getData())
                                .build()
                ).toList()
        );
    }
}
