package com.example.symptomtracker.navigation

import java.net.URLDecoder
import java.net.URLEncoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun decodePrefillItems(prefillItems: String?): List<String>? = prefillItems?.let {
    URLDecoder.decode(it, "UTF-8").removeSurrounding("[", "]").split(",")
}

fun encodePrefillItems(items: List<String>?): String? = items?.let {
    URLEncoder.encode(
        it.joinToString(separator = ",", prefix = "[", postfix = "]"),
        "UTF-8",
    )
}

fun formatDate(date: LocalDate?): String? = date?.format(DateTimeFormatter.ISO_DATE)
