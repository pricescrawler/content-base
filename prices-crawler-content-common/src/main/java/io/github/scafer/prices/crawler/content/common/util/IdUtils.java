package io.github.scafer.prices.crawler.content.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdUtils {
    public static String parse(String locale, String catalog) {
        return String.format("%s.%s", locale, catalog);
    }

    public static String parse(String locale, String catalog, String reference) {
        return String.format("%s.%s.%s", locale, catalog, reference.toLowerCase().trim());
    }
}
