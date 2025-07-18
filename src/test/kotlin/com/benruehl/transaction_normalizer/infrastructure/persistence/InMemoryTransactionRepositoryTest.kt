package com.benruehl.transaction_normalizer.infrastructure.persistence

import com.benruehl.transaction_normalizer.domain.entities.Transaction
import com.benruehl.transaction_normalizer.domain.entities.TransactionType
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toFlux
import reactor.test.StepVerifier
import java.math.BigDecimal

class InMemoryTransactionRepositoryTest {

    @Test
    fun `save should store multiple transactions for same customer`() {
        // Arrange
        val repository = InMemoryTransactionRepository()
        val transactions = listOf(aTransaction(), aTransaction(), aTransaction()).toFlux()

        // Act
        val storedData = transactions
            .flatMap { repository.save("1", it) }
            .collectList()
            .flatMapMany { repository.findByCustomerId("1") }

        // Assert
        StepVerifier.create(storedData)
            .expectNextCount(3)
            .verifyComplete()
    }

    @Test
    fun `save should store multiple transactions for different customers`() {
        // Arrange
        val repository = InMemoryTransactionRepository()
        val transactions = listOf(aTransaction(), aTransaction(), aTransaction()).toFlux()

        // Act
        transactions
            .flatMap { repository.save("1", it) }
            .flatMap { repository.save("2", it) }
            .subscribe()

        val storedDataForCustomer1 = repository.findByCustomerId("1")
        val storedDataForCustomer2 = repository.findByCustomerId("2")

        // Assert
        StepVerifier.create(storedDataForCustomer1)
            .expectNextCount(3)
            .verifyComplete()

        StepVerifier.create(storedDataForCustomer2)
            .expectNextCount(3)
            .verifyComplete()
    }

    @Test
    fun `save should append transactions to existing transaction for same customer`() {
        // Arrange
        val repository = InMemoryTransactionRepository()
        val transactions = listOf(aTransaction(), aTransaction(), aTransaction()).toFlux()

        // Act
        val storedData = transactions
            .flatMap { repository.save("1", it) }
            .flatMap { repository.save("1", it) }
            .collectList()
            .flatMapMany { repository.findByCustomerId("1") }

        // Assert
        StepVerifier.create(storedData)
            .expectNextCount(6)
            .verifyComplete()
    }

    private fun aTransaction(): Transaction {
        return Transaction(
            date = "2025-01-01",
            amount = BigDecimal("100.00"),
            amountInEur = BigDecimal("100.00"),
            currency = "Dollar",
            creditorIban = "123456789",
            creditorName = "creditor",
            normalizedPurpose = "purpose",
            bank = "bank",
            transactionType = TransactionType.DIRECT_DEBIT
        )
    }
}