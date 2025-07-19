package com.benruehl.transaction_normalizer.infrastructure.frankfurter

import com.benruehl.transaction_normalizer.application.ports.CurrencyClient
import com.benruehl.transaction_normalizer.application.ports.CurrencyRates
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Duration

@Service
class FrankfurterClient : CurrencyClient {

    private val webClient = WebClient.create("https://frankfurter.dev/")

    override fun fetchCurrencyRates(baseCurrency: String): Mono<CurrencyRates> {
        return webClient.get()
            .uri { it.path("/v1/latest").queryParam("base", baseCurrency).build() }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono<FrankfurterResponse>()
            .map { it.toCurrencyRates() }
            .doOnNext { /* TODO: logging */ }
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