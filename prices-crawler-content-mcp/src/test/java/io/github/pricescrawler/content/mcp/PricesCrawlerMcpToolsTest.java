package io.github.pricescrawler.content.mcp;

import io.github.pricescrawler.content.common.dto.catalog.CatalogDto;
import io.github.pricescrawler.content.common.dto.catalog.CategoryDto;
import io.github.pricescrawler.content.common.dto.catalog.LocaleDto;
import io.github.pricescrawler.content.common.dto.catalog.StoreDto;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.dto.product.filter.FilterProductByQueryDto;
import io.github.pricescrawler.content.common.dto.product.search.SearchProductsDto;
import io.github.pricescrawler.content.repository.catalog.CatalogDataService;
import io.github.pricescrawler.content.repository.product.history.ProductHistoryDataService;
import io.github.pricescrawler.content.service.catalog.CatalogService;
import io.github.pricescrawler.content.service.product.ProductService;
import io.github.pricescrawler.content.service.product.list.ProductListService;
import io.github.pricescrawler.content.service.product.provider.ProductServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PricesCrawlerMcpToolsTest {
    private CatalogService catalogService;
    private ProductServiceProvider productServiceProvider;
    private ProductHistoryDataService productHistoryDataService;
    private ProductListService productListService;
    private PricesCrawlerMcpTools tools;

    @BeforeEach
    void setup() {
        catalogService = mock(CatalogService.class);
        productServiceProvider = mock(ProductServiceProvider.class);
        productHistoryDataService = mock(ProductHistoryDataService.class);
        productListService = mock(ProductListService.class);
        tools = new PricesCrawlerMcpTools(catalogService, mock(CatalogDataService.class), productServiceProvider,
                productHistoryDataService, productListService);
    }

    @Test
    void listCatalogsMergesCategoriesAndSkipsInactiveEntries() {
        var store = StoreDto.builder().id("lisboa").name("Lisboa").isActive(true).build();
        var closedStore = StoreDto.builder().id("porto").name("Porto").isActive(false).build();
        var shop = CatalogDto.builder().id("pt.shop").name("Shop").isActive(true)
                .stores(List.of(store, closedStore)).build();
        var protectedShop = CatalogDto.builder().id("pt.protected").name("Protected").isActive(true)
                .isClientFetchRequired(true).build();
        var inactiveShop = CatalogDto.builder().id("pt.closed").name("Closed").isActive(false).build();
        var locale = LocaleDto.builder().id("pt").isActive(true).categories(List.of(
                CategoryDto.builder().id("supermarket").catalogs(List.of(shop, inactiveShop)).build(),
                CategoryDto.builder().id("pharmacy").catalogs(List.of(shop, protectedShop)).build())).build();
        var inactiveLocale = LocaleDto.builder().id("es").isActive(false).categories(List.of(
                CategoryDto.builder().id("supermarket").catalogs(List.of(
                        CatalogDto.builder().id("es.shop").isActive(true).build())).build())).build();
        when(catalogService.searchLocales()).thenReturn(Flux.just(locale, inactiveLocale));

        StepVerifier.create(tools.listCatalogs(null))
                .assertNext(catalogs -> {
                    assertEquals(List.of("pt.shop", "pt.protected"),
                            catalogs.stream().map(CatalogSummaryDto::id).toList());
                    var first = catalogs.getFirst();
                    assertEquals(List.of("supermarket", "pharmacy"), first.categories());
                    assertEquals(List.of(new CatalogSummaryDto.StoreSummary("lisboa", "Lisboa")), first.stores());
                    assertTrue(first.serverSearchEnabled());
                    assertFalse(catalogs.get(1).serverSearchEnabled());
                })
                .verifyComplete();
    }

    @Test
    void listCatalogsFiltersByLocale() {
        when(catalogService.searchLocaleById("pt")).thenReturn(Mono.just(LocaleDto.builder().id("pt").isActive(true).build()));

        StepVerifier.create(tools.listCatalogs(" pt "))
                .assertNext(catalogs -> assertTrue(catalogs.isEmpty()))
                .verifyComplete();
        verify(catalogService, never()).searchLocales();
    }

    @Test
    void searchProductsLimitsResultsWithoutChangingTheSource() {
        var products = IntStream.range(0, 30).mapToObj(i -> ProductDto.builder().reference("r" + i).build()).toList();
        var result = SearchProductsDto.builder().locale("pt").catalog("shop").products(products).build();
        var service = mock(ProductService.class);
        var filter = ArgumentCaptor.forClass(FilterProductByQueryDto.class);
        when(productServiceProvider.findServiceFromCatalog("pt.shop#lisboa")).thenReturn(Optional.of(service));
        when(service.searchProductByQuery(filter.capture())).thenReturn(Mono.just(result));

        StepVerifier.create(tools.searchProducts(" milk ", List.of("pt.shop#lisboa"), 5))
                .assertNext(results -> assertEquals(5, results.getFirst().getProducts().size()))
                .verifyComplete();
        assertEquals(30, result.getProducts().size());
        assertEquals("milk", filter.getValue().getQuery());
        assertEquals("lisboa", filter.getValue().getStoreId());
        assertEquals("shop#lisboa", filter.getValue().getComposedCatalogKey());
    }

    @Test
    void searchProductsUsesTheDefaultLimit() {
        var products = IntStream.range(0, 30).mapToObj(i -> ProductDto.builder().build()).toList();
        var service = mock(ProductService.class);
        when(productServiceProvider.findServiceFromCatalog("pt.shop")).thenReturn(Optional.of(service));
        when(service.searchProductByQuery(any())).thenReturn(Mono.just(SearchProductsDto.builder().products(products).build()));

        StepVerifier.create(tools.searchProducts("milk", List.of("pt.shop"), null))
                .assertNext(results -> assertEquals(PricesCrawlerMcpTools.DEFAULT_RESULTS_PER_CATALOG,
                        results.getFirst().getProducts().size()))
                .verifyComplete();
    }

    @Test
    void searchProductsRejectsUnknownCatalogs() {
        when(productServiceProvider.findServiceFromCatalog(anyString())).thenReturn(Optional.empty());

        StepVerifier.create(tools.searchProducts("milk", List.of("pt.nope"), null))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("pt.nope"))
                .verify();
    }

    @Test
    void searchProductsValidatesItsArguments() {
        var tooManyCatalogs = IntStream.rangeClosed(0, PricesCrawlerMcpTools.MAX_CATALOGS_PER_SEARCH)
                .mapToObj(i -> "pt.shop" + i).toList();

        StepVerifier.create(tools.searchProducts(" ", List.of("pt.shop"), null))
                .expectError(IllegalArgumentException.class).verify();
        StepVerifier.create(tools.searchProducts("milk", List.of(), null))
                .expectError(IllegalArgumentException.class).verify();
        StepVerifier.create(tools.searchProducts("milk", tooManyCatalogs, null))
                .expectError(IllegalArgumentException.class).verify();
        verify(productServiceProvider, never()).findServiceFromCatalog(anyString());
    }

    @Test
    void getPriceHistoryRejectsInvalidDates() {
        StepVerifier.create(tools.getPriceHistory("pt", "shop", "1", "yesterday", null))
                .expectError(IllegalArgumentException.class).verify();
        verify(productHistoryDataService, never()).findProduct(anyString(), anyString(), anyString());
    }

    @Test
    void getPriceHistoryFailsWhenTheProductIsUnknown() {
        when(productHistoryDataService.findProduct("pt", "shop", "1")).thenReturn(Mono.empty());

        StepVerifier.create(tools.getPriceHistory("pt", "shop", "1", null, null))
                .expectError(IllegalArgumentException.class).verify();
    }

    @Test
    void findProductsByBarcodeRejectsNonNumericCodes() {
        StepVerifier.create(tools.findProductsByBarcode("abc"))
                .expectError(IllegalArgumentException.class).verify();
        verify(productHistoryDataService, never()).findProductsByEanUpc(anyString());
    }

    @Test
    void getSharedListFailsWhenTheListIsUnknown() {
        when(productListService.retrieveProductList("x")).thenReturn(Mono.empty());

        StepVerifier.create(tools.getSharedList("x"))
                .expectError(IllegalArgumentException.class).verify();
    }
}
