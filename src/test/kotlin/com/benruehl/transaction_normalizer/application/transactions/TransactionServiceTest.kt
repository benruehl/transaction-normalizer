package com.benruehl.transaction_normalizer.application.transactions

import com.benruehl.transaction_normalizer.domain.repositories.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    lateinit var normalizers: List<TransactionNormalizer<*>>

    lateinit var transactionRepositoryMock: TransactionRepository

    @BeforeEach
    fun beforeEach() {
        transactionRepositoryMock = mockk<TransactionRepository>()
        every { transactionRepositoryMock.save(any(), any()) } returns Mono.empty()
    }

    @Test
    fun `import should call all normalizers`() {
        // Arrange
        val spiedNormalizers = normalizers.map { spyk(it) }
        val service = TransactionService(transactionRepositoryMock, spiedNormalizers)
        val dataToImport = listOf(aTransactionImportDto()).toFlux()

        // Act
        service.import("1", dataToImport).block()

        // Arrange
        assertThat(normalizers).isNotEmpty
        spiedNormalizers.forEach {
            verify { it.getTargetPropertyValue(any()) }
        }
    }

    @Test
    fun `import should assign normalized values`() {
        // Arrange
        val service = TransactionService(transactionRepositoryMock, normalizers)
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
}