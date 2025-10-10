package com.example.symptomtracker.core.domain.model

data class DrinkItem(
    val id: Long,
    val name: String,
) {
    constructor(name: String) : this(id = 0, name = name)
}
