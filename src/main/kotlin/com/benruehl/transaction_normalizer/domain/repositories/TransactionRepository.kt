package com.benruehl.transaction_normalizer.domain.repositories

import com.benruehl.transaction_normalizer.domain.entities.Transaction
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TransactionRepository {
    fun findAll(customerId: String): Flux<Transaction>
    fun save(customerId: String, transaction: Transaction): Mono<Transaction>
}