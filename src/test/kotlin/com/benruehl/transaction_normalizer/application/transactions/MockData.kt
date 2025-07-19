package com.benruehl.transaction_normalizer.application.transactions

import java.math.BigDecimal

fun aTransactionImportDto(): TransactionImportDto {
    return TransactionImportDto(
        bookingDate = "2024-05-28",
        valueDate = "2024-05-27",
        amount = BigDecimal("111.00"),
        currency = "EUR",
        purpose = "purpose",
        transactionCode = "PMNT-ICDT-STDO",
        creditorName = "creditor",
        creditorIban = "DE89370400440532013000",
        debtorName = "debtor",
        debtorIban = "DE44500105170648489890"
    )
}
