package com.benruehl.transaction_normalizer.application.transactions.normalizers

import com.benruehl.transaction_normalizer.application.transactions.TransactionImportDto
import com.benruehl.transaction_normalizer.application.transactions.TransactionNormalizer
import com.benruehl.transaction_normalizer.domain.entities.Transaction
import org.springframework.stereotype.Component
import kotlin.reflect.KProperty1

@Component
class PurposeNormalizer : TransactionNormalizer<String> {
    override fun getTargetProperty(): KProperty1<Transaction, String> {
        return Transaction::normalizedPurpose
    }

    override fun getTargetPropertyValue(transaction: TransactionImportDto): String {
        val purposeParts = transaction.purpose?.split("+")
        val relevantPurpose = purposeParts?.last() ?: transaction.purpose ?: ""
        return relevantPurpose

        // TODO: apply natural letter casing, e.g. 'ONLINE ÜBERWEISUNG' -> 'Online Überweisung'
    }
}