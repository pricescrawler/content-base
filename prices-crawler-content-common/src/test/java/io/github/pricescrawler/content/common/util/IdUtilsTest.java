package io.github.pricescrawler.content.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IdUtilsTest {

    @Test
    void parse_shouldReturnParsedStringWithLocaleAndCatalog() {
        var locale = "local";
        var catalog = "demo";
        assertEquals("local.demo", IdUtils.parse(locale, catalog));
    }

    @Test
    void parse_shouldReturnParsedStringWithLocaleCatalogAndReference() {
        var locale = "local";
        var catalog = "demo";
        var reference = "1";
        assertEquals("local.demo.1", IdUtils.parse(locale, catalog, reference));
    }

    @Test
    void parse_shouldReturnParsedStringWithLocaleCatalogStoreAndReference() {
        var locale = "local";
        var catalog = "demo";
        var store = "store";
        var reference = "1";
        assertEquals("local.demo#store.1", IdUtils.parse(locale, catalog, store, reference));
    }

    @Test
    void parseCatalogComposedKey_shouldReturnComposedKeyFromCatalogAndStore() {
        assertEquals("demo#store", IdUtils.parseCatalogComposeKey("demo", "store"));
    }

    @Test
    void parseCatalogFromComposedKey_shouldReturnCatalogFromComposedKey() {
        assertEquals("demo", IdUtils.extractCatalogFromComposedKey("demo#store"));
    }

    @Test
    void parseStoreFromComposedKey_shouldReturnStoreFromComposedKey() {
        assertEquals("store", IdUtils.parseStoreFromComposedKey("demo#store"));
    }

    @Test
    void parseStoreFromComposedKey_shouldReturnNullIfNoStore() {
        assertNull(IdUtils.parseStoreFromComposedKey("demo"));
    }

    @Test
    void extractLocaleFromKey() {
        assertEquals("local", IdUtils.extractLocaleFromKey("local.demo"));
    }

    @Test
    void removeLocaleFromComposedKey_shouldRemoveLocaleFromComposedKey() {
        assertEquals("demo#store.1", IdUtils.removeLocaleFromComposedKey("local.demo#store.1"));
    }

    @Test
    void removeLocaleFromComposedKey_shouldReturnSameKeyIfNoLocale() {
        var composedKey = "demo";
        var result = IdUtils.removeLocaleFromComposedKey(composedKey);
        assertEquals(composedKey, result);
    }
}

