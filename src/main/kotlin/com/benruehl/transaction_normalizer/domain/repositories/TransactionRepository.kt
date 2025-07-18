package com.benruehl.transaction_normalizer.domain.repositories

import com.benruehl.transaction_normalizer.domain.entities.Transaction
import reactor.core.publisher.Mono

interface TransactionRepository {
    fun insert(customerId: String, transaction: Transaction): Mono<Transaction>
}