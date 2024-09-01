package com.example.symptomtracker.core.domain

import com.example.symptomtracker.core.domain.model.Ingredient
import com.example.symptomtracker.core.domain.usecase.GetMealieRecipeIngredientsUseCase
import com.example.symptomtracker.core.domain.usecase.MealieRecipeIngredientsResult
import com.example.symptomtracker.core.network.MealieService
import com.example.symptomtracker.core.network.model.MealieRecipe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.HttpURLConnection

class GetMealieRecipeIngredientsUseCaseTest {
    private var mockWebServer = MockWebServer()
    private lateinit var mealieService: MealieService
    private lateinit var getMealieRecipeIngredientsUseCase: GetMealieRecipeIngredientsUseCase

    @Before
    fun setup() {
        mockWebServer.start()
        mealieService = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(MealieService::class.java)

        getMealieRecipeIngredientsUseCase = GetMealieRecipeIngredientsUseCase(mealieService)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun whenIngredientsListIsEmpty_anEmptyResponseIsReturned() = runBlocking {
        val response = MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""{"id": "1", "recipeIngredient": []}""")
        mockWebServer.enqueue(response)

        val result = getMealieRecipeIngredientsUseCase.invoke(recipeSlug = "slug")

        assertEquals(MealieRecipeIngredientsResult.Empty, result)
    }

    @Test
    fun whenIngredientsListIsNotEmpty_aSuccessResponseIsReturned() = runBlocking {
        val response = MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(
            """{"id": "1", "recipeIngredient": [
                    |{"food": {"name": "apple"}},
                    |{"food": {"name": "banana"}},
                    |{"food": {"name": ""}},
                    |{"food": {"name": "banana"}},
                    |{"food": {"name":"    "}}
                    |]}""".trimMargin()
        )

        mockWebServer.enqueue(response)

        val result = getMealieRecipeIngredientsUseCase.invoke(recipeSlug = "slug")

        assertEquals(
            MealieRecipeIngredientsResult.Success(
                listOf(
                    Ingredient("apple"),
                    Ingredient("banana"),
                )
            ),
            result,
        )
    }

    @Test
    fun whenResponseIsNotSuccessful_anErrorResponseIsReturned() = runBlocking {
        val response = MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        mockWebServer.enqueue(response)

        val result = getMealieRecipeIngredientsUseCase.invoke(recipeSlug = "slug")

        assertEquals(MealieRecipeIngredientsResult.Error(), result)
    }

    @Test
    fun whenExceptionIsThrown_anErrorResponseContainingTheErrorMessageIsReturned() = runBlocking {
        // Create mocks for testing the exception handling
        val mockMealieService: MealieService = mock(MealieService::class.java)
        val mockCall: Call<MealieRecipe> = mock(Call::class.java) as Call<MealieRecipe>
        `when`(mockMealieService.getRecipeBySlug(anyString())).thenReturn(mockCall)
        `when`(mockCall.execute()).thenThrow(IOException("Failure"))

        val useCaseWithMockedService = GetMealieRecipeIngredientsUseCase(mockMealieService)
        val result = useCaseWithMockedService.invoke(recipeSlug = "slug")

        assertEquals(MealieRecipeIngredientsResult.Error(message = "Failure"), result)
    }
}
