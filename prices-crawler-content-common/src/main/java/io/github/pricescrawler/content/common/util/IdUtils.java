package io.github.pricescrawler.content.common.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public class IdUtils {
    public static final String CATALOG_STORE_SEPARATOR = "#";
    public static final String ID_SEPARATOR = ".";
    public static final String ID_SEPARATOR_REGEX = String.format("\\%s", ID_SEPARATOR);

    public static String parse(String locale, String catalog) {
        return String.format("%s%s%s", locale, ID_SEPARATOR, catalog);
    }

    public static String parse(String locale, String catalog, String reference) {
        return String.format("%s%s%s%s%s", locale, ID_SEPARATOR, catalog, ID_SEPARATOR, reference.toLowerCase().trim());
    }

    public static String parse(String locale, String catalog, String store, String reference) {
        return parse(locale, parseCatalogComposeKey(catalog, store), reference);
    }

    public static String parseCatalogComposeKey(String catalog, String store) {
        return String.format("%s%s%s", catalog, CATALOG_STORE_SEPARATOR, store);
    }

    public static String parseStoreFromComposedKey(String value) {
        var catalogAndStore = value.split(CATALOG_STORE_SEPARATOR);

        if (catalogAndStore.length > 1) {
            return catalogAndStore[catalogAndStore.length - 1];
        }

        return null;
    }

    public static String extractCatalogFromComposedKey(String value) {
        return Arrays.stream(value.split(CATALOG_STORE_SEPARATOR)).findFirst().orElse(value);
    }

    public static String extractLocaleFromKey(String value) {
        return Arrays.stream(value.split(ID_SEPARATOR_REGEX)).findFirst().orElse(value);
    }

    public static String removeLocaleFromComposedKey(String value) {
        var localeAndStore = value.split(ID_SEPARATOR_REGEX);

        if (localeAndStore.length > 1) {
            return value.replace(String.format("%s%s", localeAndStore[0], ID_SEPARATOR), "");
        }

        return value;
    }
}
