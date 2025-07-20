package com.benruehl.transaction_normalizer.application.transactions.dto

import java.math.BigDecimal
import jakarta.xml.bind.annotation.*

@XmlRootElement(name = "Document")
@XmlAccessorType(XmlAccessType.FIELD)
data class CamtDocument(

    @field:XmlElement(name = "BkToCstmrStmt")
    var bankToCustomerStatement: BankToCustomerStatement? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class BankToCustomerStatement(

    @field:XmlElement(name = "Stmt")
    var statements: MutableList<Statement>? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class Statement(

    @field:XmlElement(name = "Id")
    var id: String? = null,

    @field:XmlElement(name = "ElctrncSeqNb")
    var electronicSequenceNumber: Int? = null,

    @field:XmlElement(name = "CreDtTm")
    var creationDateTime: String? = null,

    @field:XmlElement(name = "Acct")
    var account: Account? = null,

    @field:XmlElement(name = "Ntry")
    var entries: MutableList<Entry>? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class Account(

    @field:XmlElement(name = "Id")
    var id: AccountId? = null,

    @field:XmlElement(name = "Ccy")
    var currency: String? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class AccountId(

    @field:XmlElement(name = "IBAN")
    var iban: String? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class Entry(

    @field:XmlElement(name = "Amt")
    var amount: Amount? = null,

    @field:XmlElement(name = "CdtDbtInd")
    var creditDebitIndicator: CreditDebitIndicator? = null,

    @field:XmlElement(name = "BookgDt")
    var bookingDate: DateWrapper? = null,

    @field:XmlElement(name = "ValDt")
    var valueDate: DateWrapper? = null,

    @field:XmlElement(name = "NtryDtls")
    var entryDetails: EntryDetails? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class Amount(

    @XmlValue
    var value: BigDecimal? = null,

    @field:XmlAttribute(name = "Ccy")
    var currency: String? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class DateWrapper(

    @field:XmlElement(name = "Dt")
    var date: String? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class EntryDetails(

    @field:XmlElement(name = "TxDtls")
    var transactionDetails: TransactionDetails? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class TransactionDetails(

    @field:XmlElement(name = "RmtInf")
    var remittanceInformation: RemittanceInformation? = null,

    @field:XmlElement(name = "RltdPties")
    var relatedParties: RelatedParties? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class RemittanceInformation(

    @field:XmlElement(name = "Ustrd")
    var unstructured: String? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class RelatedParties(

    @field:XmlElement(name = "Cdtr")
    var creditor: Party? = null,

    @field:XmlElement(name = "Dbtr")
    var debtor: Party? = null,

    @field:XmlElement(name = "CdtrAcct")
    var creditorAccount: PartyAccount? = null,

    @field:XmlElement(name = "DbtrAcct")
    var debtorAccount: PartyAccount? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class Party(

    @field:XmlElement(name = "Nm")
    var name: String? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class PartyAccount(

    @field:XmlElement(name = "Id")
    var id: AccountId? = null
)

@XmlType(name = "CreditDebitIndicator")
@XmlEnum
enum class CreditDebitIndicator {
    @XmlEnumValue("CRDT")
    CRDT,

    @XmlEnumValue("DBIT")
    DBIT
}
