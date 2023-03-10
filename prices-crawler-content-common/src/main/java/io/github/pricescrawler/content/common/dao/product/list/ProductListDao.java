package io.github.pricescrawler.content.common.dao.product.list;

import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("product-list")
public class ProductListDao {
    @Id
    private String id;
    private List<ProductListItemDto> items;
    private String date;
    private Map<String, Object> data;
}
