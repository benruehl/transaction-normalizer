package com.benruehl.transaction_normalizer.application.transactions

import com.benruehl.transaction_normalizer.domain.entities.Transaction
import com.benruehl.transaction_normalizer.domain.entities.TransactionType
import com.benruehl.transaction_normalizer.domain.repositories.TransactionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.math.BigDecimal

@Service
class TransactionImportService(
    val transactionRepository: TransactionRepository
) {
    fun import(customerId: String, camtDocument: Mono<CamtDocument>): Mono<Void> {
        return import(customerId, getTransactionsFromCamtDocument(camtDocument))
    }

    fun import(customerId: String, transactions: Flux<TransactionImportDto>): Mono<Void> {
        return transactions.flatMap {
            transactionRepository.insert(customerId, it.mapToEntity())
        }.then()
    }

    private fun getTransactionsFromCamtDocument(camtDocument: Mono<CamtDocument>): Flux<TransactionImportDto> {
        return camtDocument.flatMapMany { document ->
            document.bankToCustomerStatement?.statements?.toFlux()?.flatMap { statement ->
                statement.entries?.map { it.mapToTransactionImportDto() }?.toFlux()
            }
        }
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

    private fun TransactionImportDto.mapToEntity(): Transaction {
        return Transaction(
            date = bookingDate,
            amount = amount,
            amountInEur = amount, // TODO
            currency = currency,
            creditorIban = creditorIban ?: "",
            creditorName = creditorName ?: "",
            normalizedPurpose = "", // TODO
            bank = "", // TODO
            transactionType = TransactionType.DIRECT_DEBIT, // TODO()
        )
    }
}