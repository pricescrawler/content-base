package io.github.pricescrawler.content.service.product.list;

import io.github.pricescrawler.content.common.dao.product.list.ProductListDao;
import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import io.github.pricescrawler.content.common.dto.product.ProductListShareDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.repository.product.list.ProductListDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SimpleProductListService implements ProductListService {
    protected static final int PRODUCT_LIST_PRUNE_TIME = 2;

    private final ProductListDataService productListDataService;

    @Override
    public Mono<List<ProductListItemDto>> retrieveProductList(String id) {
        return productListDataService.findProductListById(id)
                .map(ProductListDao::getItems)
                .defaultIfEmpty(Collections.emptyList());
    }

    @Override
    public Mono<ProductListShareDto> storeProductList(List<ProductListItemDto> productListItems) {
        var dateTime = DateTimeUtils.getCurrentDateTime();

        var productList = ProductListDao.builder()
                .items(productListItems)
                .date(dateTime)
                .build();

        return productListDataService.saveProductList(productList)
                .map(saved -> ProductListShareDto.builder()
                        .id(saved.getId())
                        .expirationDate(DateTimeUtils.getDateAfterDuration(dateTime, Duration.ofDays(PRODUCT_LIST_PRUNE_TIME)))
                        .build());
    }

    @Override
    public Mono<Void> deleteOutdatedProductLists() {
        return productListDataService.findAllProductList()
                .filter(item -> DateTimeUtils.getDurationBetweenDates(item.getDate(),
                        DateTimeUtils.getCurrentDateTime()).toDays() > PRODUCT_LIST_PRUNE_TIME)
                .flatMap(item -> productListDataService.deleteProductList(item.getId()))
                .then();
    }
}
