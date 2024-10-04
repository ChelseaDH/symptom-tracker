package com.example.symptomtracker.core.util

/**
 * Returns a list of items sorted based on prioritisation of whether their string representation
 * starts with the search term or contains it. Items that start with the search term are prioritised
 * over items that contain it.
 *
 * @param searchTerm The string to search for
 * @param items The list of items to search through
 * @param getItemComparisonString A function that returns the string representation of an item
 * @return A list of items sorted based on the search term
 */
fun <T> getPrioritisedSearchResults(
    searchTerm: String,
    items: List<T>,
    getItemComparisonString: (T) -> String
): List<T> {
    val trimmedSearchTerm = searchTerm.trim()

    val (startsWith, contains) = items.fold(
        Pair(
            mutableListOf<T>(),
            mutableListOf<T>()
        )
    ) { acc, item ->
        val comparisonString = getItemComparisonString(item)

        when {
            comparisonString.startsWith(trimmedSearchTerm, ignoreCase = true) -> acc.first.add(item)
            comparisonString.contains(trimmedSearchTerm, ignoreCase = true) -> acc.second.add(item)
        }

        acc
    }

    return startsWith + contains
}
