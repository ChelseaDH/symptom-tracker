package com.example.symptomtracker.core.util

fun String.toDrinkItemName(): String = this.trim().replaceFirstChar { it.uppercaseChar() }
