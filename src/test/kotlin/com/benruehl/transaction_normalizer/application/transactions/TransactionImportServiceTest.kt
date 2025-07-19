package com.benruehl.transaction_normalizer.application.transactions

import com.benruehl.transaction_normalizer.domain.repositories.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.math.BigDecimal

@SpringBootTest
class TransactionImportServiceTest {

    @Autowired
    lateinit var normalizers: List<TransactionNormalizer<*>>

    lateinit var transactionRepositoryMock: TransactionRepository

    @BeforeEach
    fun beforeEach() {
        transactionRepositoryMock = mockk<TransactionRepository>()
        every { transactionRepositoryMock.save(any(), any()) } returns Mono.empty()
    }

    @Test
    fun `import should call normalizers and assign normalized values`() {
        // Arrange
        val service = TransactionImportService(transactionRepositoryMock, normalizers)
        val dataToImport = listOf(aTransactionImportDto()).toFlux()

        // Act
        service.import("1", dataToImport).block()

        // Arrange
        verify {
            transactionRepositoryMock.save(
                "1",
                match { it.normalizedPurpose.isNotEmpty() }
            )
        }
    }

    private fun aTransactionImportDto(): TransactionImportDto {
        return TransactionImportDto(
            bookingDate = "2024-05-28",
            valueDate = "2024-05-27",
            amount = BigDecimal("111.00"),
            currency = "EUR",
            purpose = "purpose",
            transactionCode = "PMNT-ICDT-STDO",
            creditorName = "creditor",
            creditorIban = "DE89370400440532013000",
            debtorName = "debtor",
            debtorIban = "DE44500105170648489890"
        )
    }
}