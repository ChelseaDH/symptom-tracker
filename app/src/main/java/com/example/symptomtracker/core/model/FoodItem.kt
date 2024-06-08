package com.example.symptomtracker.core.model

/**
 * External data layer representation of a food item.
 */
data class FoodItem(
    val id: Long,
    val name: String,
) {
    constructor(name: String) : this(id = 0, name = name)
}
