package com.benruehl.transaction_normalizer.application.transactions

import com.benruehl.transaction_normalizer.application.utils.setDataclassProperty
import com.benruehl.transaction_normalizer.domain.entities.Transaction
import com.benruehl.transaction_normalizer.domain.entities.TransactionType
import com.benruehl.transaction_normalizer.domain.repositories.TransactionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TransactionImportService(
    val transactionRepository: TransactionRepository,
    val normalizers: List<TransactionNormalizer<*>>
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
        )

        return normalizers.fold(nonNormalizedTransaction) { foldedTransaction, nextNormalizer ->
            setDataclassProperty(
                foldedTransaction,
                nextNormalizer.getTargetProperty(),
                nextNormalizer.getTargetPropertyValue(this)
            )
        }
    }
}