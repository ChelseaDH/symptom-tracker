package com.example.symptomtracker.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FoodItemUtilsKtTest {
    @Test
    fun whenToFoodItemNameIsCalled_expectedFoodItemNameIsReturned() {
        listOf(
            arrayOf("", ""),
            arrayOf("apple   ", "Apple"),
            arrayOf("  apple", "Apple"),
            arrayOf("Apple", "Apple"),
        ).forEach { (input, expectedOutput) ->
            assertEquals(expectedOutput, input.toFoodItemName())
        }
    }
}
