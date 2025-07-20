package com.benruehl.transaction_normalizer.application.transactions.normalizers

import com.benruehl.transaction_normalizer.application.transactions.dto.TransactionImportDto
import com.benruehl.transaction_normalizer.application.transactions.TransactionNormalizer
import com.benruehl.transaction_normalizer.domain.entities.Transaction
import com.benruehl.transaction_normalizer.domain.entities.TransactionType
import com.benruehl.transaction_normalizer.domain.entities.TransactionType.*
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import kotlin.reflect.KProperty1

@Component
class TransactionTypeNormalizer : TransactionNormalizer<TransactionType> {
    override fun getTargetProperty(): KProperty1<Transaction, TransactionType> {
        return Transaction::transactionType
    }

    override fun getTargetPropertyValue(transaction: TransactionImportDto): Mono<TransactionType> {
        return transaction.transactionCode.orEmpty().let {
            when {
                it.startsWith("PMNT-DDOC") -> DIRECT_DEBIT
                it.startsWith("PMNT-IRCT") -> INTERNAL_TRANSFER
                it.startsWith("PMNT-RCDT") -> INCOMING_CREDIT
                it.startsWith("PMNT-POSD") -> CARD_PAYMENT
                it.startsWith("PMNT-ICDT") -> DIGITAL_PURCHASE
                else -> UNKNOWN
            }
        }.toMono()
    }
}