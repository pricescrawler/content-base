package io.github.pricescrawler.content.common.util;

import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@AllArgsConstructor
public class RestClient<T> {
    private static final Duration RETRY_MIN_BACKOFF = Duration.ofMillis(200);

    private final WebClient webClient;
    private final int timeoutInSec;
    private final int numberOfRetries;

    public Mono<T> getMonoRequest(String uri, MultiValueMap<String, String> queryParams, MultiValueMap<String, String> headers, ParameterizedTypeReference<T> typeReference) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParams(queryParams)
                        .build())
                .headers(httpHeaders -> httpHeaders.addAll(new HttpHeaders(headers)))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(typeReference)
                .timeout(Duration.ofSeconds(timeoutInSec))
                .retryWhen(retrySpec());
    }

    public Mono<T> postMonoRequest(String uri, MultiValueMap<String, String> queryParams, MultiValueMap<String, String> headers, String body, ParameterizedTypeReference<T> typeReference) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParams(queryParams)
                        .build())
                .headers(httpHeaders -> httpHeaders.addAll(new HttpHeaders(headers)))
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(typeReference)
                .timeout(Duration.ofSeconds(timeoutInSec))
                .retryWhen(retrySpec());
    }

    /**
     * Retries with exponential backoff instead of immediately, so a struggling catalog
     * site isn't hammered; the original error is propagated once retries run out.
     */
    private Retry retrySpec() {
        return Retry.backoff(numberOfRetries, RETRY_MIN_BACKOFF)
                .onRetryExhaustedThrow((spec, signal) -> signal.failure());
    }
}
