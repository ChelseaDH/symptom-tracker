package com.example.symptomtracker.core.util

fun String.ensureTrailingSlash(): String {
    return when {
        isBlank() -> this
        !endsWith("/") -> "$this/"
        else -> this
    }
}
