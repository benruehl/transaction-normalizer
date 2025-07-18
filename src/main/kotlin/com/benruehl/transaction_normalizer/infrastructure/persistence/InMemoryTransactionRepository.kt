package com.benruehl.transaction_normalizer.infrastructure.persistence

import com.benruehl.transaction_normalizer.domain.entities.Transaction
import com.benruehl.transaction_normalizer.domain.repositories.TransactionRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class InMemoryTransactionRepository : TransactionRepository {

//    private val db: MutableMap<String, MutableList<Transaction>> = mutableMapOf()

    override fun insert(
        customerId: String,
        transaction: Transaction
    ): Mono<Transaction> {
        return Mono.just(transaction) // TODO store data
    }
}