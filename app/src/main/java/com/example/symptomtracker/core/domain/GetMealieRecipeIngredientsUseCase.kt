package com.example.symptomtracker.core.domain

import com.example.symptomtracker.core.model.Ingredient
import com.example.symptomtracker.core.network.MealieService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetMealieRecipeIngredientsUseCase @Inject constructor(private val mealieService: MealieService) {
    suspend operator fun invoke(recipeSlug: String): MealieRecipeIngredientsResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = mealieService.getRecipeBySlug(recipeSlug).execute()

                if (!response.isSuccessful) return@withContext MealieRecipeIngredientsResult.Error()

                val ingredients = response.body()!!.recipeIngredient
                    .filter { it.food.name.isNotBlank() }
                    .distinct()
                    .map { Ingredient(it.food.name) }

                if (ingredients.isEmpty()) MealieRecipeIngredientsResult.Empty
                else MealieRecipeIngredientsResult.Success(ingredients = ingredients)
            } catch (t: Throwable) {
                MealieRecipeIngredientsResult.Error(message = t.message)
            }
        }
    }
}

sealed interface MealieRecipeIngredientsResult {
    data object Empty : MealieRecipeIngredientsResult
    data class Success(val ingredients: List<Ingredient>) : MealieRecipeIngredientsResult
    data class Error(val message: String? = null) : MealieRecipeIngredientsResult
}
