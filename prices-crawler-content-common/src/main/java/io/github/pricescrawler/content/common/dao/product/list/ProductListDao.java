package io.github.pricescrawler.content.common.dao.product.list;

import io.github.pricescrawler.content.common.dao.base.Identifiable;
import io.github.pricescrawler.content.common.dto.product.ProductListItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document("product-list")
public class ProductListDao extends Identifiable {
    private List<ProductListItemDto> items;
    private String date;
}
