package com.benruehl.transaction_normalizer.domain.entities

import java.math.BigDecimal

data class Transaction(
    val date: String,
    val amount: BigDecimal,
    val amountInEur: BigDecimal,
    val currency: String,
    val creditorIban: String,
    val creditorName: String,
    val normalizedPurpose: String,
    val bank: String,
    val transactionType: TransactionType,
)

enum class TransactionType {
    DIRECT_DEBIT, // Lastschrift
    INTERNAL_TRANSFER, // Interne Umbuchung
    INCOMING_CREDIT, // Gutschrift
    CARD_PAYMENT, // Kartenzahlung
    DIGITAL_PURCHASE,
    UNKNOWN,
}
