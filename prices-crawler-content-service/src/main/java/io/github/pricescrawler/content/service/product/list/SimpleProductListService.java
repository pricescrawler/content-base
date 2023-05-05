package io.github.pricescrawler.content.service.product.list;

import io.github.pricescrawler.content.common.dao.product.list.ProductListDao;
import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import io.github.pricescrawler.content.common.dto.product.ProductListShareDto;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.repository.product.list.ProductListDataService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
public class SimpleProductListService implements ProductListService {
    public static final int PRODUCT_LIST_PRUNE_TIME = 2;
    private final ProductListDataService productListDataService;

    public SimpleProductListService(ProductListDataService productListDataService) {
        this.productListDataService = productListDataService;
    }

    @Override
    public List<ProductListItemDto> retrieveProductList(String id) {
        var productList = productListDataService.findProductListById(id);

        if (productList.isPresent()) {
            return productList.get().getItems();
        }

        return Collections.emptyList();
    }

    @Override
    public ProductListShareDto storeProductList(List<ProductListItemDto> productListItems) {
        var dateTime = DateTimeUtils.getCurrentDateTime();

        var productList = ProductListDao.builder()
                .items(productListItems)
                .date(dateTime)
                .build();

        return ProductListShareDto.builder()
                .id(productListDataService.saveProductList(productList).getId())
                .expirationDate(DateTimeUtils.getDateAfterDuration(dateTime, Duration.ofDays(PRODUCT_LIST_PRUNE_TIME)))
                .build();
    }

    @Override
    public void deleteOutdatedProductLists() {
        for (var item : productListDataService.findAllProductList()) {
            var days = DateTimeUtils.getDurationBetweenDates(item.getDate(),
                    DateTimeUtils.getCurrentDateTime()).toDays();

            if (days > PRODUCT_LIST_PRUNE_TIME) {
                productListDataService.deleteProductList(item.getId());
            }
        }
    }
}
