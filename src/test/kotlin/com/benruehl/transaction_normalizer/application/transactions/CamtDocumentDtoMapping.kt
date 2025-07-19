package com.benruehl.transaction_normalizer.application.transactions

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Unmarshaller
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.io.StringReader
import java.math.BigDecimal
import kotlin.test.assertEquals

class CamtDocumentDtoMapping {
    
    @Test
    fun `mapToTransactionImportDtos should map multiple entries`() {
        // Arrange
        val mappings = listOf(
            aMappingWithTransactionCode(),
            aMappingWithPurpose(),
        )
        val (givenDocumentEntries, expectedDtos) = mappings.unzip()
        val camtDocument = aCamtDocument(givenDocumentEntries)
        
        // Act
        val mappedDtos = camtDocument.mapToTransactionImportDtos().collectList().block()
        
        // Assert
        assertEquals(expectedDtos, mappedDtos)
    }

    @Test
    fun `mapToTransactionImportDtos should map entries with transaction code`() {
        // Arrange
        val (givenDocumentEntry, expectedDto) = aMappingWithTransactionCode()
        val camtDocument = aCamtDocument(listOf(givenDocumentEntry))

        // Act
        val mappedDtos = camtDocument.mapToTransactionImportDtos()

        // Assert
        StepVerifier.create(mappedDtos)
            .expectNext(expectedDto)
            .verifyComplete()
    }

    @Test
    fun `mapToTransactionImportDtos should map entries with purpose`() {
        // Arrange
        val (givenDocumentEntry, expectedDto) = aMappingWithPurpose()
        val camtDocument = aCamtDocument(listOf(givenDocumentEntry))

        // Act
        val mappedDtos = camtDocument.mapToTransactionImportDtos()

        // Assert
        StepVerifier.create(mappedDtos)
            .expectNext(expectedDto)
            .verifyComplete()
    }

    private fun aMappingWithTransactionCode(): Pair<String, TransactionImportDto> {
        return """
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
        """.trimIndent() to TransactionImportDto(
            bookingDate = "2024-05-28",
            valueDate = "2024-05-27",
            amount = BigDecimal("-85.12"),
            currency = "EUR",
            purpose = null,
            transactionCode = "PMNT-ICDT-STDO",
            creditorName = "Amazon EU SARL",
            creditorIban = null,
            debtorName = null,
            debtorIban = null
        )
    }

    private fun aMappingWithPurpose(): Pair<String, TransactionImportDto> {
        return """
            <Ntry>
              <Amt Ccy="EUR">2150.00</Amt>
              <CdtDbtInd>CRDT</CdtDbtInd>
              <BookgDt>
                <Dt>2024-06-01</Dt>
              </BookgDt>
              <ValDt>
                <Dt>2024-06-01</Dt>
              </ValDt>
              <NtryDtls>
                <TxDtls>
                  <RmtInf>
                    <Ustrd>SVWX+GEHALT MAI 2024 MAX MUSTERMANN</Ustrd>
                  </RmtInf>
                  <RltdPties>
                    <Dbtr>
                      <Nm>Musterfirma GmbH</Nm>
                    </Dbtr>
                    <DbtrAcct>
                      <Id>
                        <IBAN>DE89370400440532013000</IBAN>
                      </Id>
                    </DbtrAcct>
                  </RltdPties>
                </TxDtls>
              </NtryDtls>
            </Ntry>
        """.trimIndent() to TransactionImportDto(
            bookingDate = "2024-06-01",
            valueDate = "2024-06-01",
            amount = BigDecimal("2150.00"),
            currency = "EUR",
            purpose = "SVWX+GEHALT MAI 2024 MAX MUSTERMANN",
            transactionCode = null,
            creditorName = null,
            creditorIban = null,
            debtorName = "Musterfirma GmbH",
            debtorIban = "DE89370400440532013000"
        )
    }
    
    private fun aCamtDocument(entries: List<String>): CamtDocument {
        return fromXml("""
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
                  ${entries.joinToString("\n")}
                </Stmt>
              </BkToCstmrStmt>
            </Document>
        """.trimIndent())
    }

    fun fromXml(xml: String): CamtDocument {
        val context: JAXBContext = JAXBContext.newInstance(CamtDocument::class.java)
        val unmarshaller: Unmarshaller = context.createUnmarshaller()
        return unmarshaller.unmarshal(StringReader(xml)) as CamtDocument
    }
}