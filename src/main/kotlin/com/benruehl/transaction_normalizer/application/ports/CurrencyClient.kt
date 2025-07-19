package com.benruehl.transaction_normalizer.application.ports

import reactor.core.publisher.Mono
import java.math.BigDecimal

interface CurrencyClient {
    fun fetchCurrencyRates(baseCurrency: String = "EUR"): Mono<CurrencyRates>
}

data class CurrencyRates(
    val base: String,
    val date: String,
    val rates: Map<String, BigDecimal>,
)