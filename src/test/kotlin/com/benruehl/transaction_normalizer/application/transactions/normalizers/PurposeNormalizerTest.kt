package com.benruehl.transaction_normalizer.application.transactions.normalizers

import com.benruehl.transaction_normalizer.application.transactions.aTransactionImportDto
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PurposeNormalizerTest {

    @Test
    fun `should cut off everything in front of the last plus sign`() {
        // Arrange
        val givenPurpose = aTransactionImportDto().copy(purpose = "SVWZ+SVWX+GEHALT MAI 2024 MAX MUSTERMANN")
        val expectedPurpose = "GEHALT MAI 2024 MAX MUSTERMANN"
        val normalizer = PurposeNormalizer()

        // Act
        val normalizedValue = normalizer.getTargetPropertyValue(givenPurpose).block()

        // Assert
        assertEquals(expectedPurpose, normalizedValue)
    }
}