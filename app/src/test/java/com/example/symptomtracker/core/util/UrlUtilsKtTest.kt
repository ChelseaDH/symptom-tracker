package com.example.symptomtracker.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class UrlUtilsKtTest {
    @Test
    fun expectedUrlStringReturned() {
        listOf(
            arrayOf("", ""),
            arrayOf("     ", "     "),
            arrayOf("withSlash/", "withSlash/"),
            arrayOf("withoutSlash", "withoutSlash/")
        ).forEach {
            assertEquals(it[1], it[0].ensureTrailingSlash())
        }
    }
}
