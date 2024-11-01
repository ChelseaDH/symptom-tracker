package com.example.symptomtracker.core.domain.model

data class FoodItem(
    val id: Long,
    val name: String,
) {
    constructor(name: String) : this(id = 0, name = name)
}
