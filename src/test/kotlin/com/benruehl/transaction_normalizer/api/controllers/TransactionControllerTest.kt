package com.benruehl.transaction_normalizer.api.controllers

import com.benruehl.transaction_normalizer.domain.entities.Transaction
import com.benruehl.transaction_normalizer.domain.entities.TransactionType
import com.benruehl.transaction_normalizer.infrastructure.persistence.InMemoryTransactionRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.math.BigDecimal

@SpringBootTest
class TransactionControllerTest {
    private lateinit var client: WebTestClient

    @Autowired
    lateinit var transactionRepository: InMemoryTransactionRepository

    @BeforeEach
    fun setup(context: ApplicationContext) {
        client = WebTestClient.bindToApplicationContext(context).build()
    }

    @AfterEach
    fun afterEach() {
        transactionRepository.deleteAll()
    }

    @Test
    fun `upload should return 200 when transactions are present as json`() {
        client.post().uri("/customers/1/transactions/upload")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""[
                {
                    "bookingDate": "2024-05-28",
                    "valueDate": "2024-05-27",
                    "amount": -85.12,
                    "currency": "EUR",
                    "purpose": "SVWZ+",
                    "transactionCode": "PMNT-ICDT-STDO",
                    "creditorName": "Amazon EU SARL"
                }
            ]""".trimIndent())
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `upload should return 200 when transactions are present as xml`() {
        client.post().uri("/customers/1/transactions/upload")
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue("""
                <?xml version="1.0" ?>
                <Document>
                  <BkToCstmrStmt>
                    <Stmt>
                      <Id>STATEMENT-ALL-30</Id>
                      <ElctrncSeqNb>1</ElctrncSeqNb>
                      <CreDtTm>2024-06-13T12:00:00</CreDtTm>
                      <Acct>
                        <Id>
                          <IBAN>DE44500105170648489890</IBAN>
                        </Id>
                        <Ccy>EUR</Ccy>
                      </Acct>
                      <Ntry>
                        <Amt Ccy="EUR">-85.12</Amt>
                        <CdtDbtInd>DBIT</CdtDbtInd>
                        <BookgDt>
                          <Dt>2024-05-28</Dt>
                        </BookgDt>
                        <ValDt>
                          <Dt>2024-05-27</Dt>
                        </ValDt>
                        <NtryDtls>
                          <TxDtls>
                            <RmtInf>
                              <Ustrd>PMNT-ICDT-STDO</Ustrd>
                            </RmtInf>
                            <RltdPties>
                              <Cdtr>
                                <Nm>Amazon EU SARL</Nm>
                              </Cdtr>
                              <CdtrAcct>
                                <Id/>
                              </CdtrAcct>
                            </RltdPties>
                          </TxDtls>
                        </NtryDtls>
                      </Ntry>
                    </Stmt>
                  </BkToCstmrStmt>
                </Document>
            """.trimIndent())
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `query should return 200 when transactions exist`() {
        // Arrange
        val existingTransaction = aTransaction().copy(date = "2025-10-10")
        transactionRepository.save("1", existingTransaction).block()

        // Act + Assert
        client.get().uri("/customers/1/transactions/normalized")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].date").isEqualTo("2025-10-10")
    }

    private fun aTransaction(): Transaction {
        return Transaction(
            date = "2025-01-01",
            amount = BigDecimal("10.00"),
            amountInEur = BigDecimal("10.00"),
            currency = "EUR",
            creditorIban = "iban",
            creditorName = "creditor",
            normalizedPurpose = "purpose",
            bank = "bank",
            transactionType = TransactionType.DIRECT_DEBIT
        )
    }
}