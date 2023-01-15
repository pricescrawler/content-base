package io.github.scafer.prices.crawler.content.service.product.list;

import io.github.scafer.prices.crawler.content.common.dao.product.list.ProductListDao;
import io.github.scafer.prices.crawler.content.common.dto.product.ProductListItemDto;
import io.github.scafer.prices.crawler.content.common.util.DateTimeUtils;
import io.github.scafer.prices.crawler.content.repository.product.list.ProductListDataService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SimpleProductListService implements ProductListService {
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
    public String saveProductList(List<ProductListItemDto> productListItems) {
        var productList = ProductListDao.builder()
                .items(productListItems)
                .date(DateTimeUtils.getCurrentDateTime())
                .build();

        return productListDataService.saveProductList(productList).getId();
    }

    @Override
    public void deleteOutdatedProductLists() {
        for (var item : productListDataService.findAllProductList()) {
            var days = DateTimeUtils.durationBetweenDates(item.getDate(), DateTimeUtils.getCurrentDateTime()).toDays();

            if (days > 2) {
                productListDataService.deleteProductList(item.getId());
            }
        }
    }
}
