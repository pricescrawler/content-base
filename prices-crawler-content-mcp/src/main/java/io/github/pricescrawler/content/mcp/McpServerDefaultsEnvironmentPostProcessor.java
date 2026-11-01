package io.github.pricescrawler.content.mcp;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

/**
 * Gives the Spring AI MCP server the defaults of this project, added with the lowest
 * precedence so any {@code spring.ai.mcp.server.*} property set by an implementation wins.
 * <p>
 * The server follows the {@code prices.crawler.controller.mcp.enabled} toggle, like the
 * REST controllers: adding this module as a dependency is not enough to expose {@code /mcp}.
 */
public class McpServerDefaultsEnvironmentPostProcessor implements EnvironmentPostProcessor {
    static final String ENABLED_PROPERTY = "prices.crawler.controller.mcp.enabled";

    private static final String INSTRUCTIONS = """
            Search and compare retail product prices collected by Prices Crawler.
            Start with list_catalogs to get catalog ids ("<locale>.<catalog>", optionally \
            "<locale>.<catalog>#<store>"), then use search_products for live prices. \
            get_price_history returns the stored price history of one product, and \
            find_products_by_barcode finds the same product (EAN/UPC) across catalogs.""";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        environment.getPropertySources().addLast(new MapPropertySource("pricesCrawlerMcpDefaults", Map.of(
                "spring.ai.mcp.server.enabled", "${" + ENABLED_PROPERTY + ":false}",
                "spring.ai.mcp.server.name", "prices-crawler",
                "spring.ai.mcp.server.protocol", "STATELESS",
                "spring.ai.mcp.server.type", "ASYNC",
                "spring.ai.mcp.server.request-timeout", "60s",
                "spring.ai.mcp.server.instructions", INSTRUCTIONS)));
    }
}
