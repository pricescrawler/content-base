package io.github.pricescrawler.content.mcp;

import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Talks JSON-RPC to the stateless MCP endpoint, as an MCP client would.
 */
class McpServerTest extends BaseSpringBootTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void shouldInitializeWithTheServerName() {
        var result = rpc("initialize", Map.of(
                "protocolVersion", "2025-06-18",
                "capabilities", Map.of(),
                "clientInfo", Map.of("name", "test", "version", "1.0")));

        assertEquals("prices-crawler", result.path("serverInfo").path("name").asString());
    }

    @Test
    void shouldListOnlyTheReadOnlyTools() {
        var tools = rpc("tools/list", Map.of()).path("tools");
        var names = StreamSupport.stream(tools.spliterator(), false).map(tool -> tool.path("name").asString()).toList();

        assertEquals(List.of("find_products_by_barcode", "get_price_history", "get_shared_list", "list_catalogs",
                "search_products"), names.stream().sorted().toList());
        tools.forEach(tool -> assertTrue(tool.path("annotations").path("readOnlyHint").asBoolean(), tool.toString()));
    }

    @Test
    void shouldListCatalogs() {
        var catalogs = callTool("list_catalogs", Map.of());

        assertEquals("local.demo", catalogs.path(0).path("id").asString());
        assertEquals(List.of("demo-category"), List.of(catalogs.path(0).path("categories").path(0).asString()));
        assertFalse(catalogs.path(0).path("serverSearchEnabled").asBoolean());
    }

    @Test
    void shouldReturnThePriceHistory() {
        var product = callTool("get_price_history", Map.of("locale", "local", "catalog", "demo", "reference", "1"));

        assertEquals("Demo Product 1", product.path("name").asString());
        assertEquals(1, product.path("prices").size());
    }

    @Test
    void shouldFindProductsByBarcode() {
        var products = callTool("find_products_by_barcode", Map.of("eanUpc", "123456789"));

        assertEquals(1, products.size());
        assertEquals("demo", products.path(0).path("catalog").asString());
    }

    @Test
    void shouldReportToolErrorsAsResults() {
        var result = rpc("tools/call", Map.of("name", "search_products",
                "arguments", Map.of("query", "milk", "catalogs", List.of("local.fake-catalog"))));

        assertTrue(result.path("isError").asBoolean());
        assertTrue(result.path("content").path(0).path("text").asString().contains("local.fake-catalog"));
    }

    private JsonNode callTool(String name, Map<String, Object> arguments) {
        var result = rpc("tools/call", Map.of("name", name, "arguments", arguments));
        assertFalse(result.path("isError").asBoolean(), result.toString());

        return MAPPER.readTree(result.path("content").path(0).path("text").asString());
    }

    private JsonNode rpc(String method, Map<String, Object> params) {
        var response = webTestClient.post().uri("/mcp")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM)
                .bodyValue(Map.of("jsonrpc", "2.0", "id", 1, "method", method, "params", params))
                .exchange()
                .expectStatus().isOk()
                .expectBody(JsonNode.class)
                .returnResult()
                .getResponseBody();

        assertTrue(response != null && response.has("result"), String.valueOf(response));
        return response.path("result");
    }
}
