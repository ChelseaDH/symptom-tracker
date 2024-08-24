package com.example.symptomtracker.feature.mealie

import android.webkit.URLUtil
import com.example.symptomtracker.core.domain.GetMealieRecipeIngredientsUseCase
import com.example.symptomtracker.core.domain.MealieRecipeIngredientsResult
import com.example.symptomtracker.core.model.Ingredient
import com.example.symptomtracker.core.model.TextInput
import com.example.symptomtracker.core.model.TextValidationError
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`

class MealieImportFoodViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val getMealieRecipeIngredients = mock(GetMealieRecipeIngredientsUseCase::class.java)
    private lateinit var viewModel: MealieImportFoodViewModel

    @Before
    fun setup() {
        viewModel =
            MealieImportFoodViewModel(getMealieRecipeIngredients = getMealieRecipeIngredients)
    }

    @Test
    fun whenInitialised_stateHoldsDefaultValues() = assertEquals(
        MealieImportFoodState(
            url = TextInput(value = ""), searchState = null, canImport = false,
        ),
        viewModel.uiState.value,
    )

    @Test
    fun whenUpdateUrlIsCalled_stateUpdatesAccordingly() = runTest {
        viewModel.uiState.value = MealieImportFoodState(url = TextInput(value = "url"))

        viewModel.updateUrl("newUrl")

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

        viewModel.clearUrl()

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

            viewModel.removeIngredientFromImport(Ingredient("Banana"))

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

                viewModel.search()

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

                viewModel.search()

                assertEquals(
                    expectedState,
                    viewModel.uiState.value,
                )
            }
        }
    }
}
