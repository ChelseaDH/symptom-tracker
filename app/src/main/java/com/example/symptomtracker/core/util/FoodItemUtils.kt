package com.example.symptomtracker.core.util

fun String.toFoodItemName(): String = this.trim().replaceFirstChar { it.uppercaseChar() }
