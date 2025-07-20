package com.benruehl.transaction_normalizer.infrastructure.frankfurter

import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.wiremock.spring.EnableWireMock
import reactor.test.StepVerifier

@SpringBootTest
@EnableWireMock
class FrankfurterClientTest(
    @Value("\${wiremock.server.baseUrl}") private val wireMockUrl: String
) {

    @Test
    fun `should return currency rates`() {
        // Arrange
        val baseCurrency = "EUR"
        val date = "2025-01-01"
        val frankfurterResponseMock = aFrankurterResponse(baseCurrency, date)
        val client = FrankfurterClient(wireMockUrl)
        stubFor(
            get("/v1/latest?base=$baseCurrency")
                .willReturn(okJson(frankfurterResponseMock))
        )

        // Act
        val response = client.fetchCurrencyRates()

        // Assert
        StepVerifier.create(response)
            .expectNextMatches {
                it.base == baseCurrency &&
                it.date == date &&
                it.rates.isNotEmpty()
            }
            .verifyComplete()
    }

    fun aFrankurterResponse(baseCurrency: String, date: String): String {
        return """{
            "amount":1.0,
            "base":"$baseCurrency",
            "date":"$date",
            "rates":{
                "AUD":1.7852,
                "BGN":1.9558,
                "BRL":6.4699,
                "CAD":1.5984,
                "CHF":0.9324,
                "CNY":8.3623,
                "CZK":24.623,
                "DKK":7.4636,
                "GBP":0.8656,
                "HKD":9.1439,
                "HUF":399.06,
                "IDR":18996,
                "ILS":3.9119,
                "INR":100.35,
                "ISK":142.0,
                "JPY":172.94,
                "KRW":1619.52,
                "MXN":21.809,
                "MYR":4.9425,
                "NOK":11.8335,
                "NZD":1.9493,
                "PHP":66.431,
                "PLN":4.2493,
                "RON":5.0728,
                "SEK":11.2505,
                "SGD":1.4943,
                "THB":37.688,
                "TRY":46.987,
                "USD":1.165,
                "ZAR":20.635
            }
        }""".trimIndent()
    }
}