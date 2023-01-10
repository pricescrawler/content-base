package io.github.scafer.prices.crawler.content.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange()
                .pathMatchers("/actuator/health").permitAll()
                .pathMatchers("/actuator/**").authenticated()
                .anyExchange().permitAll()
                .and()
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic();

        return http.build();
    }
}
