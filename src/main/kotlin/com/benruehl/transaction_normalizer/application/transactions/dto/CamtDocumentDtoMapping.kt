package com.benruehl.transaction_normalizer.application.transactions.dto

import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.math.BigDecimal

fun CamtDocument.mapToTransactionImportDtos(): Flux<TransactionImportDto> {
    return bankToCustomerStatement?.statements?.toFlux()?.flatMap { statement ->
        statement.entries?.map { it.mapToTransactionImportDto() }?.toFlux()
    } ?: Flux.just()
}

private fun Entry.mapToTransactionImportDto(): TransactionImportDto {
    return TransactionImportDto(
        bookingDate = bookingDate?.date ?: "",
        valueDate = valueDate?.date ?: "",
        amount = amount?.value ?: BigDecimal(""),
        currency = amount?.currency ?: "",
        purpose = entryDetails?.transactionDetails?.remittanceInformation?.unstructured?.let {
            if (it.startsWith("PMNT-")) null else it
        },
        transactionCode = entryDetails?.transactionDetails?.remittanceInformation?.unstructured?.let {
            if (it.startsWith("PMNT-")) it else null
        },
        creditorName = entryDetails?.transactionDetails?.relatedParties?.creditor?.name,
        creditorIban = entryDetails?.transactionDetails?.relatedParties?.creditorAccount?.id?.iban,
        debtorName = entryDetails?.transactionDetails?.relatedParties?.debtor?.name,
        debtorIban = entryDetails?.transactionDetails?.relatedParties?.debtorAccount?.id?.iban
    )
}