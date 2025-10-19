package com.example.symptomtracker.feature.mealie

import android.webkit.URLUtil
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.designsystem.component.TextValidationError
import com.example.symptomtracker.core.domain.model.Ingredient
import com.example.symptomtracker.core.domain.usecase.GetMealieRecipeIngredientsUseCase
import com.example.symptomtracker.core.domain.usecase.MealieRecipeIngredientsResult
import com.example.symptomtracker.navigation.DATE_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MealieImportFoodViewModel @Inject constructor(
    private val getMealieRecipeIngredients: GetMealieRecipeIngredientsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MealieImportFoodState())
    val uiState: StateFlow<MealieImportFoodState> = _uiState.asStateFlow()
    private val dateArg: String? = savedStateHandle[DATE_ARG]
    val date: LocalDate? = dateArg?.let(LocalDate::parse)

    fun handleEvent(event: MealieImportFoodEvent) {
        when (event) {
            is MealieImportFoodEvent.UpdateUrl -> updateUrl(url = event.url)
            is MealieImportFoodEvent.ClearUrl -> clearUrl()
            is MealieImportFoodEvent.Search -> search()
            is MealieImportFoodEvent.RemoveIngredientFromImport -> removeIngredientFromImport(
                ingredient = event.ingredient
            )
        }
    }

    private fun updateUrl(url: String) {
        _uiState.update { currentState ->
            currentState.copy(url = TextInput(value = url))
        }
    }

    private fun clearUrl() {
        _uiState.update { currentState ->
            currentState.copy(
                url = TextInput(value = "")
            )
        }
    }

    private fun search() {
        val currentUrl = _uiState.value.url.value

        val validationError = TextInput(value = currentUrl).findValidationError(
            errors = listOf(TextValidationError.BLANK, TextValidationError.INVALID),
            validityCheck = { URLUtil.isValidUrl(it) },
        )

        if (validationError != null) {
            _uiState.update { currentState ->
                currentState.copy(
                    url = currentState.url.copy(validationError = validationError)
                )
            }
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                searchState = MealieImportSearchState.Loading,
                canImport = false,
                url = currentState.url.copy(validationError = null),
            )
        }

        val recipeSlug = currentUrl.split("/").last()

        viewModelScope.launch {
            val result = getMealieRecipeIngredients(recipeSlug)

            _uiState.update { currentState ->
                when (result) {
                    is MealieRecipeIngredientsResult.Empty -> currentState.copy(
                        searchState = MealieImportSearchState.Error.NoIngredients,
                        canImport = false,
                    )

                    is MealieRecipeIngredientsResult.Success -> currentState.copy(
                        searchState = MealieImportSearchState.Success(
                            ingredientsToImport = result.ingredients
                        ),
                        canImport = true,
                    )

                    is MealieRecipeIngredientsResult.Error -> currentState.copy(
                        searchState = MealieImportSearchState.Error.ApiFailure(result.message),
                        canImport = false,
                    )
                }
            }
        }
    }

    private fun removeIngredientFromImport(ingredient: Ingredient) {
        _uiState.update { currentState ->
            val successState = currentState.searchState as? MealieImportSearchState.Success
                ?: return@update currentState

            val filteredIngredients = successState.ingredientsToImport.filter { it != ingredient }

            currentState.copy(
                searchState = MealieImportSearchState.Success(ingredientsToImport = filteredIngredients),
                canImport = filteredIngredients.isNotEmpty(),
            )
        }
    }
}

data class MealieImportFoodState(
    val url: TextInput = TextInput(value = ""),
    val searchState: MealieImportSearchState? = null,
    val canImport: Boolean = false,
) {
    val ingredientNames: List<String>
        get() = (searchState as? MealieImportSearchState.Success)
            ?.ingredientsToImport
            ?.map { it.name }
            .orEmpty()
}

sealed interface MealieImportSearchState {
    data object Loading : MealieImportSearchState
    data class Success(val ingredientsToImport: List<Ingredient>) : MealieImportSearchState
    sealed interface Error : MealieImportSearchState {
        data object NoIngredients : Error
        data class ApiFailure(val message: String?) : Error
    }
}

sealed interface MealieImportFoodEvent {
    data class UpdateUrl(val url: String) : MealieImportFoodEvent
    data object ClearUrl : MealieImportFoodEvent
    data object Search : MealieImportFoodEvent
    data class RemoveIngredientFromImport(val ingredient: Ingredient) : MealieImportFoodEvent
}
