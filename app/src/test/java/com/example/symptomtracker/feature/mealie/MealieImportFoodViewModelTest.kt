package com.example.symptomtracker.feature.mealie

import android.webkit.URLUtil
import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.designsystem.component.TextValidationError
import com.example.symptomtracker.core.domain.model.Ingredient
import com.example.symptomtracker.core.domain.usecase.GetMealieRecipeIngredientsUseCase
import com.example.symptomtracker.core.domain.usecase.MealieRecipeIngredientsResult
import com.example.symptomtracker.navigation.DATE_ARG
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import java.time.LocalDate

class MealieImportFoodViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val getMealieRecipeIngredients = mock(GetMealieRecipeIngredientsUseCase::class.java)
    private lateinit var viewModel: MealieImportFoodViewModel

    @Before
    fun setup() {
        viewModel = MealieImportFoodViewModel(
            getMealieRecipeIngredients = getMealieRecipeIngredients,
            savedStateHandle = SavedStateHandle(),
        )
    }

    @Test
    fun initialisation_whenNoArgsAreSet_uiStateContainsDefaultValues() = runTest {
        assertEquals(
            MealieImportFoodState(
                url = TextInput(value = ""), searchState = null, canImport = false,
            ),
            viewModel.uiState.value,
        )
        assertNull(viewModel.date)
    }

    @Test
    fun initialisation_whenArgsAreSet_uiStateContainsArgValues() = runTest {
        val viewModel = MealieImportFoodViewModel(
            getMealieRecipeIngredients = getMealieRecipeIngredients,
            savedStateHandle = SavedStateHandle(
                mapOf(DATE_ARG to "2025-05-01")
            )
        )

        assertEquals(
            MealieImportFoodState(
                url = TextInput(value = ""), searchState = null, canImport = false,
            ),
            viewModel.uiState.value,
        )
        assertEquals(LocalDate.parse("2025-05-01"), viewModel.date)
    }

    @Test
    fun whenUpdateUrlIsCalled_stateUpdatesAccordingly() = runTest {
        viewModel.uiState.value = MealieImportFoodState(url = TextInput(value = "url"))

        viewModel.handleEvent(MealieImportFoodEvent.UpdateUrl("newUrl"))

        assertEquals(
            MealieImportFoodState(
                url = TextInput(value = "newUrl"), searchState = null, canImport = false,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun whenClearUrlIsCalled_stateUpdatesAccordingly() = runTest {
        viewModel.uiState.value = MealieImportFoodState(url = TextInput(value = "url"))

        viewModel.handleEvent(MealieImportFoodEvent.ClearUrl)

        assertEquals(
            MealieImportFoodState(
                url = TextInput(value = ""), searchState = null, canImport = false,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun whenRemoveIngredientsFromImportIsCalled_stateUpdatesAccordingly() = mapOf(
        MealieImportSearchState.Success(
            ingredientsToImport = listOf(
                Ingredient("Apple"),
                Ingredient("banana"),
            )
        ) to MealieImportFoodState(
            searchState = MealieImportSearchState.Success(
                ingredientsToImport = listOf(
                    Ingredient("Apple")
                )
            ),
            canImport = true,
        ),
        MealieImportSearchState.Success(
            ingredientsToImport = listOf(
                Ingredient("Banana")
            )
        ) to MealieImportFoodState(
            searchState = MealieImportSearchState.Success(
                ingredientsToImport = emptyList()
            ),
            canImport = false,
        ),
    ).forEach { (initialSearchState, stateAfter) ->
        runTest {
            viewModel.uiState.value = MealieImportFoodState(
                searchState = initialSearchState,
                canImport = true,
            )

            viewModel.handleEvent(MealieImportFoodEvent.RemoveIngredientFromImport(Ingredient("Banana")))

            assertEquals(stateAfter, viewModel.uiState.value)
        }
    }

    @Test
    fun whenGetIngredientNamesIsCalled_applicableNamesAreReturnedFromSearchState() = mapOf(
        MealieImportSearchState.Loading to emptyList(),
        MealieImportSearchState.Error.NoIngredients to emptyList(),
        MealieImportSearchState.Error.ApiFailure(message = "Error occurred") to emptyList(),
        MealieImportSearchState.Success(ingredientsToImport = emptyList()) to emptyList(),
        MealieImportSearchState.Success(
            ingredientsToImport = listOf(
                Ingredient("Apple"),
                Ingredient("banana"),
            )
        ) to listOf("Apple", "Banana"),
    ).forEach { (searchState, expectedResult) ->
        runTest {
            viewModel.uiState.value = MealieImportFoodState(
                url = TextInput(value = "url"), searchState = searchState,
            )

            val result = viewModel.getIngredientNames()

            assertEquals(expectedResult, result)
        }
    }

    @Test
    fun givenTheSearchUrlIsInvalid_whenSearchIsCalled_theStateIsUpdatedToContainTheError() = mapOf(
        MealieImportFoodState(url = TextInput(value = "")) to MealieImportFoodState(
            url = TextInput(value = "", validationError = TextValidationError.BLANK),
            searchState = null,
            canImport = false,
        ), MealieImportFoodState(url = TextInput(value = "notAUrl")) to MealieImportFoodState(
            url = TextInput(
                value = "notAUrl", validationError = TextValidationError.INVALID
            ),
            searchState = null,
            canImport = false,
        )
    ).forEach { (stateBefore, stateAfter) ->
        runTest {
            viewModel.uiState.value = stateBefore

            mockStatic(URLUtil::class.java).use { mock ->
                mock.`when`<Boolean> { URLUtil.isValidUrl("notAUrl") }.thenReturn(false)

                viewModel.handleEvent(MealieImportFoodEvent.Search)

                assertEquals(stateAfter, viewModel.uiState.value)
            }
        }
    }

    @Test
    fun givenTheSearchUrlIsValid_whenSearchIsCalled_theSearchStateIsUpdatedAccordingly() = mapOf(
        MealieRecipeIngredientsResult.Empty to MealieImportFoodState(
            url = TextInput(value = "https://test.mealie.co/banana-bread"),
            searchState = MealieImportSearchState.Error.NoIngredients,
            canImport = false,
        ),
        MealieRecipeIngredientsResult.Error(message = "Invalid request") to MealieImportFoodState(
            url = TextInput(value = "https://test.mealie.co/banana-bread"),
            searchState = MealieImportSearchState.Error.ApiFailure(
                message = "Invalid request"
            ),
            canImport = false,
        ),
        MealieRecipeIngredientsResult.Success(ingredients = listOf(Ingredient("Banana"))) to MealieImportFoodState(
            url = TextInput(value = "https://test.mealie.co/banana-bread"),
            searchState = MealieImportSearchState.Success(
                ingredientsToImport = listOf(Ingredient("Banana"))
            ),
            canImport = true,
        ),
    ).forEach { (searchResult, expectedState) ->
        runTest {
            viewModel.uiState.value = MealieImportFoodState(
                url = TextInput(value = "https://test.mealie.co/banana-bread"),
                searchState = null,
                canImport = false,
            )

            mockStatic(URLUtil::class.java).use { mock ->
                mock.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co/banana-bread") }
                    .thenReturn(true)
                `when`(
                    getMealieRecipeIngredients.invoke("banana-bread")
                ).thenReturn(searchResult)

                viewModel.handleEvent(MealieImportFoodEvent.Search)

                assertEquals(
                    expectedState,
                    viewModel.uiState.value,
                )
            }
        }
    }
}
