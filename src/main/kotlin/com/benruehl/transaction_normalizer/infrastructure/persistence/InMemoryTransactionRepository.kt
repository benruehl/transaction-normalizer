package com.benruehl.transaction_normalizer.infrastructure.persistence

import com.benruehl.transaction_normalizer.domain.entities.Transaction
import com.benruehl.transaction_normalizer.domain.repositories.TransactionRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@Repository
class InMemoryTransactionRepository : TransactionRepository {

    private val db: MutableMap<String, MutableList<Transaction>> = mutableMapOf()

    override fun save(
        customerId: String,
        transaction: Transaction
    ): Mono<Transaction> {
        db.getOrPut(customerId) { mutableListOf() }
            .add(transaction)
        return Mono.just(transaction)
    }

    override fun findAll(customerId: String): Flux<Transaction> {
        return (db[customerId] ?: emptyList()).toFlux()
    }

    fun deleteAll() {
        db.clear()
    }
}