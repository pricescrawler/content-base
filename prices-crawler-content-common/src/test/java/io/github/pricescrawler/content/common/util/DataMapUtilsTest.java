package io.github.pricescrawler.content.common.util;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataMapUtilsTest {
    @Test
    void getMapPublicKeys_shouldReturnEmptyMap_whenInputMapIsNull() {
        var result = DataMapUtils.getMapPublicKeys(null);
        assertEquals(Collections.emptyMap(), result);
    }

    @Test
    void getMapPublicKeys_shouldReturnMapWithPublicKeysOnly() {
        var inputMap = new HashMap<String, Object>();
        inputMap.put("key1.public", "value1");
        inputMap.put("key2", "value2");
        inputMap.put("key3.public", "value3");

        var result = DataMapUtils.getMapPublicKeys(inputMap);
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value3", result.get("key3"));
    }
}