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
        // First set an initial URL
        viewModel.handleEvent(MealieImportFoodEvent.UpdateUrl("url"))

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
        // First set an initial URL
        viewModel.handleEvent(MealieImportFoodEvent.UpdateUrl("url"))

        viewModel.handleEvent(MealieImportFoodEvent.ClearUrl)

        assertEquals(
            MealieImportFoodState(
                url = TextInput(value = ""), searchState = null, canImport = false,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun whenRemoveIngredientsFromImportIsCalled_stateUpdatesAccordingly() = runTest {
        // Test case 1: Removing one ingredient from multiple
        viewModel.handleEvent(MealieImportFoodEvent.UpdateUrl("https://test.mealie.co/recipe"))

        mockStatic(URLUtil::class.java).use { mock ->
            mock.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co/recipe") }
                .thenReturn(true)

            `when`(getMealieRecipeIngredients.invoke("recipe")).thenReturn(
                MealieRecipeIngredientsResult.Success(
                    ingredients = listOf(Ingredient("Apple"), Ingredient("Banana"))
                )
            )

            viewModel.handleEvent(MealieImportFoodEvent.Search)

            assertEquals(
                MealieImportFoodState(
                    url = TextInput(value = "https://test.mealie.co/recipe"),
                    searchState = MealieImportSearchState.Success(
                        ingredientsToImport = listOf(Ingredient("Apple"), Ingredient("Banana"))
                    ),
                    canImport = true,
                ),
                viewModel.uiState.value
            )

            viewModel.handleEvent(MealieImportFoodEvent.RemoveIngredientFromImport(Ingredient("Banana")))

            assertEquals(
                MealieImportFoodState(
                    url = TextInput(value = "https://test.mealie.co/recipe"),
                    searchState = MealieImportSearchState.Success(
                        ingredientsToImport = listOf(Ingredient("Apple"))
                    ),
                    canImport = true,
                ),
                viewModel.uiState.value
            )
        }

        // Test case 2: Removing last ingredient
        viewModel.handleEvent(MealieImportFoodEvent.UpdateUrl("https://test.mealie.co/recipe2"))

        mockStatic(URLUtil::class.java).use { mock ->
            mock.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co/recipe2") }
                .thenReturn(true)

            `when`(getMealieRecipeIngredients.invoke("recipe2")).thenReturn(
                MealieRecipeIngredientsResult.Success(
                    ingredients = listOf(Ingredient("Banana"))
                )
            )

            viewModel.handleEvent(MealieImportFoodEvent.Search)

            viewModel.handleEvent(MealieImportFoodEvent.RemoveIngredientFromImport(Ingredient("Banana")))

            assertEquals(
                MealieImportFoodState(
                    url = TextInput(value = "https://test.mealie.co/recipe2"),
                    searchState = MealieImportSearchState.Success(
                        ingredientsToImport = emptyList()
                    ),
                    canImport = false,
                ),
                viewModel.uiState.value
            )
        }
    }

    @Test
    fun ingredientNamesProperty_returnsApplicableNamesFromSearchState() = runTest {
        // Test case 1: Empty state
        assertEquals(emptyList<String>(), viewModel.uiState.value.ingredientNames)

        // Test case 2: After successful search
        viewModel.handleEvent(MealieImportFoodEvent.UpdateUrl("https://test.mealie.co/recipe"))

        mockStatic(URLUtil::class.java).use { mock ->
            mock.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co/recipe") }
                .thenReturn(true)

            `when`(getMealieRecipeIngredients.invoke("recipe")).thenReturn(
                MealieRecipeIngredientsResult.Success(
                    ingredients = listOf(Ingredient("Apple"), Ingredient("Banana"))
                )
            )

            viewModel.handleEvent(MealieImportFoodEvent.Search)

            assertEquals(listOf("Apple", "Banana"), viewModel.uiState.value.ingredientNames)
        }

        // Test case 3: Empty ingredients list
        viewModel.handleEvent(MealieImportFoodEvent.UpdateUrl("https://test.mealie.co/recipe2"))

        mockStatic(URLUtil::class.java).use { mock ->
            mock.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co/recipe2") }
                .thenReturn(true)

            `when`(getMealieRecipeIngredients.invoke("recipe2")).thenReturn(
                MealieRecipeIngredientsResult.Success(ingredients = emptyList())
            )

            viewModel.handleEvent(MealieImportFoodEvent.Search)

            assertEquals(emptyList<String>(), viewModel.uiState.value.ingredientNames)
        }

        // Test case 4: Error states return empty
        viewModel.handleEvent(MealieImportFoodEvent.UpdateUrl("https://test.mealie.co/recipe3"))

        mockStatic(URLUtil::class.java).use { mock ->
            mock.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co/recipe3") }
                .thenReturn(true)

            `when`(getMealieRecipeIngredients.invoke("recipe3")).thenReturn(
                MealieRecipeIngredientsResult.Error(message = "Error occurred")
            )

            viewModel.handleEvent(MealieImportFoodEvent.Search)

            assertEquals(emptyList<String>(), viewModel.uiState.value.ingredientNames)
        }
    }

    @Test
    fun givenTheSearchUrlIsInvalid_whenSearchIsCalled_theStateIsUpdatedToContainTheError() = runTest {
        // Test case 1: Empty URL
        viewModel.handleEvent(MealieImportFoodEvent.Search)

        assertEquals(
            MealieImportFoodState(
                url = TextInput(value = "", validationError = TextValidationError.BLANK),
                searchState = null,
                canImport = false,
            ),
            viewModel.uiState.value
        )

        // Test case 2: Invalid URL
        viewModel.handleEvent(MealieImportFoodEvent.UpdateUrl("notAUrl"))

        mockStatic(URLUtil::class.java).use { mock ->
            mock.`when`<Boolean> { URLUtil.isValidUrl("notAUrl") }.thenReturn(false)

            viewModel.handleEvent(MealieImportFoodEvent.Search)

            assertEquals(
                MealieImportFoodState(
                    url = TextInput(
                        value = "notAUrl", validationError = TextValidationError.INVALID
                    ),
                    searchState = null,
                    canImport = false,
                ),
                viewModel.uiState.value
            )
        }
    }

    @Test
    fun givenTheSearchUrlIsValid_whenSearchIsCalled_theSearchStateIsUpdatedAccordingly() = runTest {
        mockStatic(URLUtil::class.java).use { mock ->
            mock.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co/banana-bread") }
                .thenReturn(true)

            // Test case 1: Empty result
            viewModel.handleEvent(MealieImportFoodEvent.UpdateUrl("https://test.mealie.co/banana-bread"))

            `when`(getMealieRecipeIngredients.invoke("banana-bread"))
                .thenReturn(MealieRecipeIngredientsResult.Empty)

            viewModel.handleEvent(MealieImportFoodEvent.Search)

            assertEquals(
                MealieImportFoodState(
                    url = TextInput(value = "https://test.mealie.co/banana-bread"),
                    searchState = MealieImportSearchState.Error.NoIngredients,
                    canImport = false,
                ),
                viewModel.uiState.value
            )

            // Test case 2: API Error
            viewModel.handleEvent(MealieImportFoodEvent.UpdateUrl("https://test.mealie.co/banana-bread"))

            `when`(getMealieRecipeIngredients.invoke("banana-bread"))
                .thenReturn(MealieRecipeIngredientsResult.Error(message = "Invalid request"))

            viewModel.handleEvent(MealieImportFoodEvent.Search)

            assertEquals(
                MealieImportFoodState(
                    url = TextInput(value = "https://test.mealie.co/banana-bread"),
                    searchState = MealieImportSearchState.Error.ApiFailure(
                        message = "Invalid request"
                    ),
                    canImport = false,
                ),
                viewModel.uiState.value
            )

            // Test case 3: Success
            viewModel.handleEvent(MealieImportFoodEvent.UpdateUrl("https://test.mealie.co/banana-bread"))

            `when`(getMealieRecipeIngredients.invoke("banana-bread"))
                .thenReturn(MealieRecipeIngredientsResult.Success(ingredients = listOf(Ingredient("Banana"))))

            viewModel.handleEvent(MealieImportFoodEvent.Search)

            assertEquals(
                MealieImportFoodState(
                    url = TextInput(value = "https://test.mealie.co/banana-bread"),
                    searchState = MealieImportSearchState.Success(
                        ingredientsToImport = listOf(Ingredient("Banana"))
                    ),
                    canImport = true,
                ),
                viewModel.uiState.value
            )
        }
    }
}
