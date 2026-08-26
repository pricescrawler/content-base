package io.github.pricescrawler.content.controller.product;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import io.github.pricescrawler.content.common.dto.product.parser.ProductContentDto;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

class ProductContentParserControllerTest extends BaseSpringBootTest {
    @Test
    void shouldParseSingleProductFromContentSuccessfully() {
        webTestClient.post().uri("/api/v1/products/parser")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProductContentDto.builder().catalog("local.demo").build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldParseProductListFromContentSuccessfully() {
        webTestClient.post().uri("/api/v1/products/parser/list")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProductContentDto.builder().catalog("local.demo").build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnNotFoundForInvalidSingleProductCatalog() {
        webTestClient.post().uri("/api/v1/products/parser")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProductContentDto.builder().catalog("dummy.dummy").build())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturnNotFoundForInvalidProductListCatalog() {
        webTestClient.post().uri("/api/v1/products/parser/list")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProductContentDto.builder().catalog("dummy.dummy").build())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturnForbiddenForSingleProductWhenCatalogDoesNotAllowClientFetch() {
        catalogDataRepository.save(restrictedCatalogDao()).block();

        webTestClient.post().uri("/api/v1/products/parser")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProductContentDto.builder().catalog("local.restricted").build())
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void shouldReturnForbiddenForProductListWhenCatalogDoesNotAllowClientFetch() {
        catalogDataRepository.save(restrictedCatalogDao()).block();

        webTestClient.post().uri("/api/v1/products/parser/list")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProductContentDto.builder().catalog("local.restricted").build())
                .exchange()
                .expectStatus().isForbidden();
    }

    /**
     * A real, persisted catalog that simply hasn't opted into
     * {@code clientFetchRequired.public} — distinct from {@code dummy.dummy}, which
     * doesn't exist at all, so the 403-vs-404 distinction is actually exercised.
     */
    private CatalogDao restrictedCatalogDao() {
        return CatalogDao.builder()
                .id("local.restricted")
                .name("Restricted")
                .locales(List.of("local"))
                .categories(List.of("demo-category"))
                .build();
    }
}
