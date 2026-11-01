package io.github.pricescrawler.content.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

    /**
     * The MCP tools are read-only and return the same data as the public REST API, so
     * {@code /mcp} is public by default. Set this to require basic auth on it.
     */
    @Value("${prices.crawler.controller.mcp.authenticated:false}")
    private boolean mcpAuthenticated;

    @Value("${spring.ai.mcp.server.streamable-http.mcp-endpoint:/mcp}")
    private String mcpEndpoint;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> {
                    exchanges.pathMatchers("/actuator/health").permitAll()
                            .pathMatchers("/actuator/**").authenticated();
                    if (mcpAuthenticated) {
                        exchanges.pathMatchers(mcpEndpoint, mcpEndpoint + "/**").authenticated();
                    }
                    exchanges.anyExchange().permitAll();
                })
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
