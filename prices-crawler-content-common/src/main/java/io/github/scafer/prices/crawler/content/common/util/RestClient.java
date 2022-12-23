package io.github.scafer.prices.crawler.content.common.util;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Log4j2
@AllArgsConstructor
public class RestClient<T> {
    private final WebClient webClient;
    private final int timeoutInSec;
    private final int numberOfRetries;

    public CompletableFuture<T> getMonoRequest(String uri, MultiValueMap<String, String> queryParams, MultiValueMap<String, String> headers, ParameterizedTypeReference<T> typeReference) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParams(queryParams)
                        .build())
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(typeReference)
                .timeout(Duration.ofSeconds(timeoutInSec))
                .retry(numberOfRetries)
                .toFuture();
    }

    public CompletableFuture<T> postMonoRequest(String uri, MultiValueMap<String, String> queryParams, MultiValueMap<String, String> headers, String body, ParameterizedTypeReference<T> typeReference) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParams(queryParams)
                        .build())
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(Mono.just(body), String.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(typeReference)
                .timeout(Duration.ofSeconds(timeoutInSec))
                .retry(numberOfRetries)
                .toFuture();
    }
}
