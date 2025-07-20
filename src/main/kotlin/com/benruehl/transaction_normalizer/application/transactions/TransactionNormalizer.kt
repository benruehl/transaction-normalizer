package com.benruehl.transaction_normalizer.application.transactions

import com.benruehl.transaction_normalizer.application.transactions.dto.TransactionImportDto
import com.benruehl.transaction_normalizer.domain.entities.Transaction
import reactor.core.publisher.Mono
import kotlin.reflect.KProperty1

interface TransactionNormalizer<T> {
    fun getTargetProperty(): KProperty1<Transaction, T>
    fun getTargetPropertyValue(transaction: TransactionImportDto): Mono<T>
}