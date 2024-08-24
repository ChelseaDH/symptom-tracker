package com.example.symptomtracker.core.model

import com.example.symptomtracker.core.util.toFoodItemName

data class Ingredient(private var _name: String) {
    init {
        _name = _name.toFoodItemName()
    }

    val name get() = _name
}
