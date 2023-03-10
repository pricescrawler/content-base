package io.github.pricescrawler.content.common.util;

import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

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
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(typeReference)
                .timeout(Duration.ofSeconds(timeoutInSec))
                .retry(numberOfRetries)
                .toFuture();
    }
}