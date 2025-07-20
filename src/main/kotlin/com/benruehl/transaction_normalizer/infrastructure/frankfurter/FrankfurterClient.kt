package com.benruehl.transaction_normalizer.infrastructure.frankfurter

import com.benruehl.transaction_normalizer.application.ports.CurrencyClient
import com.benruehl.transaction_normalizer.application.ports.CurrencyRates
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Duration
import java.util.concurrent.TimeUnit

@Service
class FrankfurterClient(
    private val baseUrl: String = "https://frankfurter.dev/"
) : CurrencyClient {

    private val webClient: WebClient by lazy { WebClient.create(baseUrl) }

    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(5, TimeUnit.SECONDS)
        .maximumSize(100)
        .build<String, CurrencyRates>()

    override fun fetchCurrencyRates(baseCurrency: String): Mono<CurrencyRates> {
        cache.getIfPresent(baseCurrency)?.let {
            return Mono.just(it)
        }
        return webClient.get()
            .uri { it.path("/v1/latest").queryParam("base", baseCurrency).build() }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono<FrankfurterResponse>()
            .map { it.toCurrencyRates() }
            .doOnNext { /* TODO: logging */ }
            .doOnNext { cache.put(baseCurrency, it) }
            .timeout(Duration.ofSeconds(1))
    }
}

private data class FrankfurterResponse(
    val base: String,
    val date: String,
    val rates: Map<String, BigDecimal>,
)

private fun FrankfurterResponse.toCurrencyRates(): CurrencyRates {
    return CurrencyRates(
        base = base,
        date = date,
        rates = rates,
    )
}