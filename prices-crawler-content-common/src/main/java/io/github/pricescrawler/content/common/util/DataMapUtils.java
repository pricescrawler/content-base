package io.github.pricescrawler.content.common.util;

import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class DataMapUtils {
    private static final String EMPTY = "";
    private static final String PUBLIC = ".public";

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
        return value.replace(PUBLIC, EMPTY);
    }
}
