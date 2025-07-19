package com.benruehl.transaction_normalizer.application.transactions.normalizers

import com.benruehl.transaction_normalizer.application.ports.CurrencyClient
import com.benruehl.transaction_normalizer.application.ports.CurrencyRates
import com.benruehl.transaction_normalizer.application.transactions.aTransactionImportDto
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.core.publisher.Mono
import java.math.BigDecimal
import kotlin.test.assertEquals

class AmountInEurNormalizerTest {

    lateinit var currencyClientMock: CurrencyClient

    @BeforeEach
    fun beforeEach() {
        currencyClientMock = mockk<CurrencyClient>()
        every { currencyClientMock.fetchCurrencyRates(any()) } returns Mono.empty()
    }

    @Test
    fun `should return amount when currency is already EUR`() {
        // Arrange
        val givenTransaction = aTransactionImportDto().copy(
            amount = BigDecimal("10.50"),
            currency = "EUR",
        )
        val normalizer = AmountInEurNormalizer(currencyClientMock)

        // Act
        val normalizedValue = normalizer.getTargetPropertyValue(givenTransaction).block()

        // Assert
        assertEquals(BigDecimal("10.50"), normalizedValue)
    }

    @Test
    fun `should return amount multiplied with currency rate when currency is not in EUR`() {
        // Arrange
        val givenTransaction = aTransactionImportDto().copy(
            amount = BigDecimal("10.50"),
            currency = "USD",
        )
        every { currencyClientMock.fetchCurrencyRates("EUR") } returns Mono.just(
            CurrencyRates(
                base = "EUR",
                date = "2025-01-01",
                rates = mapOf("USD" to BigDecimal("1.165"))
            )
        )
        val normalizer = AmountInEurNormalizer(currencyClientMock)

        // Act
        val normalizedValue = normalizer.getTargetPropertyValue(givenTransaction).block()

        // Assert
        assertEquals(BigDecimal("10.50") * BigDecimal("1.165"), normalizedValue)
    }

    @Test
    fun `should throw when currency is not in currency client`() {
        // Arrange
        val givenTransaction = aTransactionImportDto().copy(
            amount = BigDecimal("10.50"),
            currency = "USD",
        )
        every { currencyClientMock.fetchCurrencyRates("EUR") } returns Mono.just(
            CurrencyRates(
                base = "EUR",
                date = "2025-01-01",
                rates = mapOf("AUD" to BigDecimal("1.0"))
            )
        )
        val normalizer = AmountInEurNormalizer(currencyClientMock)

        // Act + Assert
        assertThrows<IllegalArgumentException> {
            normalizer.getTargetPropertyValue(givenTransaction).block()
        }
    }
}