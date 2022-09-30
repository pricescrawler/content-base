package io.github.scafer.prices.crawler.content.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataMapUtils {

    public static final String PUBLIC = ".public";

    public static Map<String, Object> getMapPublicKeys(Map<String, Object> map) {
        if (map == null) {
            return Collections.emptyMap();
        }

        return map.entrySet()
                .stream()
                .filter(value -> value.getKey().contains(PUBLIC))
                .collect(Collectors.toMap(value -> removeString(value.getKey()), Map.Entry::getValue));
    }

    private static String removeString(String value) {
        return value.replace(PUBLIC, "");
    }
}
