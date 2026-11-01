package io.github.pricescrawler.content.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class McpServerDefaultsEnvironmentPostProcessorTest {

    @Test
    void serverIsDisabledUnlessTheToggleIsSet() {
        assertEquals("false", process(Map.of()).getProperty("spring.ai.mcp.server.enabled"));
    }

    @Test
    void serverFollowsTheToggle() {
        var environment = process(Map.of(McpServerDefaultsEnvironmentPostProcessor.ENABLED_PROPERTY, "true"));

        assertEquals("true", environment.getProperty("spring.ai.mcp.server.enabled"));
        assertEquals("STATELESS", environment.getProperty("spring.ai.mcp.server.protocol"));
    }

    @Test
    void explicitServerPropertiesWin() {
        var environment = process(Map.of(
                McpServerDefaultsEnvironmentPostProcessor.ENABLED_PROPERTY, "true",
                "spring.ai.mcp.server.name", "custom"));

        assertEquals("custom", environment.getProperty("spring.ai.mcp.server.name"));
    }

    private static StandardEnvironment process(Map<String, Object> properties) {
        var environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("test", properties));
        new McpServerDefaultsEnvironmentPostProcessor().postProcessEnvironment(environment, new SpringApplication());

        return environment;
    }
}
