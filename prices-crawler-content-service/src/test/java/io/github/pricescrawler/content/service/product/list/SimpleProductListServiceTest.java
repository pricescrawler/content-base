package io.github.pricescrawler.content.service.product.list;

import io.github.pricescrawler.content.common.dao.product.list.ProductListDao;
import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import io.github.pricescrawler.content.repository.product.list.ProductListDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleProductListServiceTest {
    @Mock
    private ProductListDataService productListDataService;

    @InjectMocks
    private SimpleProductListService productListService;

    @Test
    void retrieveProductList() {
        var id = "123";
        var date = "2024-02-19";
        var productListItems = List.of(new ProductListItemDto());

        when(productListDataService.findProductListById(id)).thenReturn(Optional.of(new ProductListDao(productListItems, date)));

        var retrievedList = productListService.retrieveProductList(id);
        assertEquals(productListItems.size(), retrievedList.size());
    }

    @Test
    void retrieveProductList_empty() {
        var retrievedList = productListService.retrieveProductList("dummy");
        assertTrue(retrievedList.isEmpty());
    }

    @Test
    void storeProductList() {
        var productListItems = List.of(new ProductListItemDto());
        var date = "2024-02-19";

        when(productListDataService.saveProductList(any(ProductListDao.class))).thenReturn(new ProductListDao(productListItems, date));

        var productListShareDto = productListService.storeProductList(productListItems);
        assertNull(productListShareDto.getId());
        assertNotNull(productListShareDto.getExpirationDate());
    }

    @Test
    void deleteOutdatedProductLists() {
        var date = "2024-01-01T00:00:00.000000000Z";
        var productListDao1 = new ProductListDao(List.of(), date);
        var productListDao2 = new ProductListDao(new ArrayList<>(), date);
        var productListDaoList = List.of(productListDao1, productListDao2);

        when(productListDataService.findAllProductList()).thenReturn(productListDaoList);

        productListService.deleteOutdatedProductLists();
        verify(productListDataService, times(2)).deleteProductList(null);
    }
}
