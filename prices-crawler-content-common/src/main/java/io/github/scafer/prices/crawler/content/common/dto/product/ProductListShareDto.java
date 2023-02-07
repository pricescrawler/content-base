package io.github.scafer.prices.crawler.content.common.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListShareDto {
    private String id;
    private String expirationDate;
}
