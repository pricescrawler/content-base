package io.github.pricescrawler.content.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IdUtilsTest {

    @Test
    void parse_ShouldReturnParsedStringWithLocaleAndCatalog() {
        String locale = "local";
        String catalog = "demo";
        String expected = "local.demo";

        String result = IdUtils.parse(locale, catalog);
        assertEquals(expected, result);
    }

    @Test
    void parse_ShouldReturnParsedStringWithLocaleCatalogAndReference() {
        String locale = "local";
        String catalog = "demo";
        String reference = "1";
        String expected = "local.demo.1";

        String result = IdUtils.parse(locale, catalog, reference);
        assertEquals(expected, result);
    }

    @Test
    void parse_ShouldReturnParsedStringWithLocaleCatalogStoreAndReference() {
        String locale = "local";
        String catalog = "demo";
        String store = "store";
        String reference = "1";
        String expected = "local.demo#store.1";

        String result = IdUtils.parse(locale, catalog, store, reference);
        assertEquals(expected, result);
    }

    @Test
    void parseCatalogComposeKey_ShouldReturnComposedKeyFromCatalogAndStore() {
        String catalog = "demo";
        String store = "store";
        String expected = "demo#store";

        String result = IdUtils.parseCatalogComposeKey(catalog, store);
        assertEquals(expected, result);
    }

    @Test
    void parseCatalogFromComposedKey_ShouldReturnCatalogFromComposedKey() {
        String composedKey = "demo#store";
        String expected = "demo";

        String result = IdUtils.parseCatalogFromComposedKey(composedKey);
        assertEquals(expected, result);
    }

    @Test
    void parseStoreFromComposedKey_ShouldReturnStoreFromComposedKey() {
        String composedKey = "demo#store";
        String expected = "store";

        String result = IdUtils.parseStoreFromComposedKey(composedKey);
        assertEquals(expected, result);
    }

    @Test
    void parseStoreFromComposedKey_ShouldReturnNullIfNoStore() {
        String composedKey = "demo";
        String result = IdUtils.parseStoreFromComposedKey(composedKey);
        assertNull(result);
    }

    @Test
    void removeLocaleFromComposedKey_ShouldRemoveLocaleFromComposedKey() {
        String composedKey = "local.demo#store.1";
        String expected = "demo#store.1";

        String result = IdUtils.removeLocaleFromComposedKey(composedKey);
        assertEquals(expected, result);
    }

    @Test
    void removeLocaleFromComposedKey_ShouldReturnSameKeyIfNoLocale() {
        String composedKey = "demo";
        String result = IdUtils.removeLocaleFromComposedKey(composedKey);
        assertEquals(composedKey, result);
    }
}

