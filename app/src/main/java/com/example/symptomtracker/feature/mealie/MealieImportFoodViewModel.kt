package com.example.symptomtracker.feature.mealie

import android.webkit.URLUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.designsystem.component.TextValidationError
import com.example.symptomtracker.core.domain.usecase.GetMealieRecipeIngredientsUseCase
import com.example.symptomtracker.core.domain.usecase.MealieRecipeIngredientsResult
import com.example.symptomtracker.core.domain.model.Ingredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealieImportFoodViewModel @Inject constructor(private val getMealieRecipeIngredients: GetMealieRecipeIngredientsUseCase) :
    ViewModel() {
    var uiState = MutableStateFlow(MealieImportFoodState())

    fun updateUrl(url: String) {
        uiState.value = uiState.value.copy(url = TextInput(value = url))
    }

    fun clearUrl() {
        uiState.value = uiState.value.copy(url = TextInput(value = ""))
    }

    fun search() {
        checkUrlInputValidity()
        if (uiState.value.url.validationError != null) return

        uiState.value =
            uiState.value.copy(
                searchState = MealieImportSearchState.Loading,
                canImport = false,
                url = uiState.value.url.copy(validationError = null)
            )

        val recipeSlug = uiState.value.url.value.split("/").last()

        viewModelScope.launch {
            uiState.value = when (val result = getMealieRecipeIngredients(recipeSlug)) {
                is MealieRecipeIngredientsResult.Empty -> uiState.value.copy(
                    searchState = MealieImportSearchState.Error.NoIngredients,
                    canImport = false
                )

                is MealieRecipeIngredientsResult.Success -> uiState.value.copy(
                    searchState = MealieImportSearchState.Success(
                        ingredientsToImport = result.ingredients
                    ),
                    canImport = true
                )

                is MealieRecipeIngredientsResult.Error -> uiState.value.copy(
                    searchState = MealieImportSearchState.Error.ApiFailure(result.message),
                    canImport = false,
                )
            }
        }
    }

    fun removeIngredientFromImport(ingredient: Ingredient) {
        if (uiState.value.searchState is MealieImportSearchState.Success) {
            val ingredients =
                (uiState.value.searchState as MealieImportSearchState.Success).ingredientsToImport.filter { it != ingredient }

            uiState.value =
                uiState.value.copy(
                    searchState = MealieImportSearchState.Success(ingredientsToImport = ingredients),
                    canImport = ingredients.isNotEmpty()
                )
        }
    }

    fun getIngredientNames(): List<String> =
        (uiState.value.searchState as? MealieImportSearchState.Success)?.ingredientsToImport?.map { it.name }
            ?: listOf()

    private fun checkUrlInputValidity() {
        val validationError = uiState.value.url.findValidationError(
            errors = listOf(
                TextValidationError.BLANK,
                TextValidationError.INVALID
            ),
            validityCheck = { URLUtil.isValidUrl(it) },
        )

        uiState.value =
            uiState.value.copy(url = uiState.value.url.copy(validationError = validationError))
    }
}

data class MealieImportFoodState(
    val url: TextInput = TextInput(value = ""),
    val searchState: MealieImportSearchState? = null,
    val canImport: Boolean = false,
)

sealed interface MealieImportSearchState {
    data object Loading : MealieImportSearchState
    data class Success(val ingredientsToImport: List<Ingredient>) : MealieImportSearchState
    sealed interface Error : MealieImportSearchState {
        data object NoIngredients : Error
        data class ApiFailure(val message: String?) : Error
    }
}
