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
        return import(customerId, camtDocument.flatMapMany { it.mapToTransactionImportDtos() })
    }

    fun import(customerId: String, transactions: Flux<TransactionImportDto>): Mono<Void> {
        return transactions.flatMap {
            transactionRepository.save(customerId, it.mapToEntity())
        }.then()
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