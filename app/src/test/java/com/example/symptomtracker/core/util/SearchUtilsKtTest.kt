package com.example.symptomtracker.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class SearchUtilsKtTest {
    private val items =
        listOf("apple pie", "banana", "banana bread", "carrot cake", "bread", "apricot")

    @Test
    fun whenSearchIsCalled_expectedItemsAreReturned() {
        listOf(
            arrayOf(
                "a",
                listOf("apple pie", "apricot", "banana", "banana bread", "carrot cake", "bread")
            ),
            arrayOf("Apple", listOf("apple pie")),
            arrayOf("bread", listOf("bread", "banana bread")),
            arrayOf("  banana   ", listOf("banana", "banana bread")),
            arrayOf("", items),
            arrayOf("raspberry", emptyList<String>()),
        ).forEach { (searchTerm, expectedOutput) ->
            assertEquals(
                expectedOutput,
                getPrioritisedSearchResults(searchTerm.toString(), items) { it })
        }
    }
}
