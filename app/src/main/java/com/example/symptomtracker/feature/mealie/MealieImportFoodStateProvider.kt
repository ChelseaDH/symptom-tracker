package com.example.symptomtracker.feature.mealie

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.core.model.Ingredient
import com.example.symptomtracker.core.model.TextInput
import com.example.symptomtracker.core.model.TextValidationError

class MealieImportFoodStateProvider : PreviewParameterProvider<MealieImportFoodState> {
    override val values: Sequence<MealieImportFoodState>
        get() = sequenceOf(
            MealieImportFoodState(),
            MealieImportFoodState(
                url = TextInput(value = "url", validationError = null),
                searchState = null
            ),
            MealieImportFoodState(
                url = TextInput(
                    value = "   ",
                    validationError = TextValidationError.BLANK
                ), searchState = null
            ),
            MealieImportFoodState(
                url = TextInput(
                    value = "invalid url",
                    validationError = TextValidationError.INVALID
                ),
                searchState = null
            ),
            MealieImportFoodState(
                url = TextInput(value = "url", validationError = null),
                searchState = MealieImportSearchState.Loading
            ),
            MealieImportFoodState(
                url = TextInput(value = "url", validationError = null),
                searchState = MealieImportSearchState.Success(
                    ingredientsToImport = listOf(
                        Ingredient("apple"),
                        Ingredient("banana"),
                    )
                )
            ),
            MealieImportFoodState(
                url = TextInput(value = "url", validationError = null),
                searchState = MealieImportSearchState.Error.NoIngredients
            ),
            MealieImportFoodState(
                url = TextInput(value = "url", validationError = null),
                searchState = MealieImportSearchState.Error.ApiFailure(null)
            ),
            MealieImportFoodState(
                url = TextInput(value = "url", validationError = null),
                searchState = MealieImportSearchState.Error.ApiFailure("Failed to retrieve recipe")
            ),
        )
}
