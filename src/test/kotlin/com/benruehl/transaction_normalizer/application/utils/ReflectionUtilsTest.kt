package com.benruehl.transaction_normalizer.application.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ReflectionUtilsTest {

    @Test
    fun `setDataclassProperty should overwrite property when args are valid`() {
        // Arrange
        val original = Dummy(dummyProperty = "123")
        val newValue = "456"

        // Act
        val result = setDataclassProperty(original, Dummy::dummyProperty, newValue)

        // Assert
        assertEquals(result.dummyProperty, newValue)
    }

    @Test
    fun `setDataclassProperty should throw IllegalArgumentException when datatype does not match`() {
        // Arrange
        val original = Dummy(dummyProperty = "123")
        val newValue = true

        // Act + Assert
        assertThrows<IllegalArgumentException> {
            setDataclassProperty(original, Dummy::dummyProperty, newValue)
        }
    }

    data class Dummy(
        val dummyProperty: String? = null
    )
}