package com.benruehl.transaction_normalizer.application.transactions.normalizers

import com.benruehl.transaction_normalizer.application.ports.CurrencyClient
import com.benruehl.transaction_normalizer.application.transactions.TransactionImportDto
import com.benruehl.transaction_normalizer.application.transactions.TransactionNormalizer
import com.benruehl.transaction_normalizer.domain.entities.Transaction
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.math.BigDecimal
import kotlin.reflect.KProperty1

@Component
class AmountInEurNormalizer(
    val currencyClient: CurrencyClient
) : TransactionNormalizer<BigDecimal> {

    override fun getTargetProperty(): KProperty1<Transaction, BigDecimal> {
        return Transaction::amountInEur
    }

    override fun getTargetPropertyValue(transaction: TransactionImportDto): Mono<BigDecimal> {
        if (transaction.currency == "EUR") {
            return Mono.just(transaction.amount)
        }
        return currencyClient
            .fetchCurrencyRates("EUR")
            .flatMap {
                val rate = it.rates[transaction.currency]
                if (rate != null) {
                    Mono.just(rate * transaction.amount)
                } else {
                    Mono.error(IllegalArgumentException("Unsupported currency: ${transaction.currency}"))
                }
            }
    }
}