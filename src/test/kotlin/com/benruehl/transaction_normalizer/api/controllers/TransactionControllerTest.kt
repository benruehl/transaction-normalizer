package com.benruehl.transaction_normalizer.api.controllers

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
class TransactionControllerTest {
    private lateinit var client: WebTestClient

    @BeforeEach
    fun setup(context: ApplicationContext) {
        client = WebTestClient.bindToApplicationContext(context).build()
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
}