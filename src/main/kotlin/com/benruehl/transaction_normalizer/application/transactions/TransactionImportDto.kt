package com.benruehl.transaction_normalizer.application.transactions

import java.math.BigDecimal

data class TransactionImportDto(
    val bookingDate: String,
    val valueDate: String,
    val amount: BigDecimal,
    val currency: String,
    val purpose: String? = null,
    val transactionCode: String? = null,
    val creditorName: String? = null,
    val creditorIban: String? = null,
    val debtorName: String? = null,
    val debtorIban: String? = null
)