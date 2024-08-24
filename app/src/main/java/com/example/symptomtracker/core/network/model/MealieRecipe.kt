package com.example.symptomtracker.core.network.model

data class MealieRecipe(
    val id: String,
    val recipeIngredient: List<MealieIngredient>,
)

data class MealieIngredient(
    val food: MealieFood
)

data class MealieFood(
    val name: String
)
