package com.benruehl.transaction_normalizer.application.transactions

import com.benruehl.transaction_normalizer.application.transactions.dto.CamtDocument
import com.benruehl.transaction_normalizer.application.transactions.dto.TransactionImportDto
import com.benruehl.transaction_normalizer.application.transactions.dto.mapToTransactionImportDtos
import com.benruehl.transaction_normalizer.application.utils.setDataclassProperty
import com.benruehl.transaction_normalizer.domain.entities.Transaction
import com.benruehl.transaction_normalizer.domain.entities.TransactionType
import com.benruehl.transaction_normalizer.domain.repositories.TransactionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class TransactionService(
    val transactionRepository: TransactionRepository,
    val normalizers: List<TransactionNormalizer<*>>
) {
    fun query(customerId: String): Flux<Transaction> {
        return transactionRepository.findAll(customerId)
    }

    fun import(customerId: String, camtDocument: Mono<CamtDocument>): Mono<Void> {
        return import(customerId, camtDocument.flatMapMany { it.mapToTransactionImportDtos() })
    }

    fun import(customerId: String, transactions: Flux<TransactionImportDto>): Mono<Void> {
        return transactions
            .flatMap { it.mapToEntity() }
            .flatMap { transactionRepository.save(customerId, it) }
            .then()
    }

    private fun TransactionImportDto.mapToEntity(): Mono<Transaction> {
        val nonNormalizedTransaction = Transaction(
            date = bookingDate,
            amount = amount,
            amountInEur = amount,
            currency = currency,
            creditorIban = creditorIban ?: "",
            creditorName = creditorName ?: "",
            normalizedPurpose = "",
            bank = "",
            transactionType = TransactionType.DIRECT_DEBIT,
        ).toMono()

        return normalizers.fold (nonNormalizedTransaction) { reducedTransaction, nextNormalizer ->
            reducedTransaction.flatMap { transaction ->
                nextNormalizer.getTargetPropertyValue(this).map {
                    setDataclassProperty(transaction, nextNormalizer.getTargetProperty(), it)
                }
            }
        }
    }
}