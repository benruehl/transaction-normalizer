package com.benruehl.transaction_normalizer.application.transactions.normalizers

import com.benruehl.transaction_normalizer.application.transactions.aTransactionImportDto
import com.benruehl.transaction_normalizer.domain.entities.TransactionType.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TransactionTypeNormalizerTest {

    @Test
    fun `should categorize direct debit`() {
        // Arrange
        val directDebitCodes = listOf(
            "PMNT-DDOC-STDO",
            "PMNT-DDOC-INSC",
        )
        val givenTransactions = directDebitCodes.map { aTransactionImportDto().copy(transactionCode = it) }
        val normalizer = TransactionTypeNormalizer()

        // Act
        val normalizedValues = givenTransactions.map { normalizer.getTargetPropertyValue(it).block() }

        // Assert
        normalizedValues.forEach {
            assertEquals(DIRECT_DEBIT, it)
        }
    }

    @Test
    fun `should categorize internal transfer`() {
        // Arrange
        val directDebitCodes = listOf(
            "PMNT-IRCT-STDO",
            "PMNT-IRCT-OTHR",
        )
        val givenTransactions = directDebitCodes.map { aTransactionImportDto().copy(transactionCode = it) }
        val normalizer = TransactionTypeNormalizer()

        // Act
        val normalizedValues = givenTransactions.map { normalizer.getTargetPropertyValue(it).block() }

        // Assert
        normalizedValues.forEach {
            assertEquals(INTERNAL_TRANSFER, it)
        }
    }

    @Test
    fun `should categorize incoming credit`() {
        // Arrange
        val directDebitCodes = listOf(
            "PMNT-RCDT-STDO",
            "PMNT-RCDT-RENT",
            "PMNT-RCDT-MEDC",
            "PMNT-RCDT-CHAR",
        )
        val givenTransactions = directDebitCodes.map { aTransactionImportDto().copy(transactionCode = it) }
        val normalizer = TransactionTypeNormalizer()

        // Act
        val normalizedValues = givenTransactions.map { normalizer.getTargetPropertyValue(it).block() }

        // Assert
        normalizedValues.forEach {
            assertEquals(INCOMING_CREDIT, it)
        }
    }

    @Test
    fun `should categorize card payment`() {
        // Arrange
        val directDebitCodes = listOf(
            "PMNT-POSD-STDO",
        )
        val givenTransactions = directDebitCodes.map { aTransactionImportDto().copy(transactionCode = it) }
        val normalizer = TransactionTypeNormalizer()

        // Act
        val normalizedValues = givenTransactions.map { normalizer.getTargetPropertyValue(it).block() }

        // Assert
        normalizedValues.forEach {
            assertEquals(CARD_PAYMENT, it)
        }
    }

    @Test
    fun `should categorize digital purchase`() {
        // Arrange
        val directDebitCodes = listOf(
            "PMNT-ICDT-DIGT",
        )
        val givenTransactions = directDebitCodes.map { aTransactionImportDto().copy(transactionCode = it) }
        val normalizer = TransactionTypeNormalizer()

        // Act
        val normalizedValues = givenTransactions.map { normalizer.getTargetPropertyValue(it).block() }

        // Assert
        normalizedValues.forEach {
            assertEquals(DIGITAL_PURCHASE, it)
        }
    }
}