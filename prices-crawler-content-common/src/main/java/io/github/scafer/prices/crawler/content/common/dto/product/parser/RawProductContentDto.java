package io.github.scafer.prices.crawler.content.common.dto.product.parser;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawProductContentDto {
    private String url;
    @JsonAlias("body")
    private String content;
    private String catalog;
    private String date;
}
