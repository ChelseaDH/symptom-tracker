package com.example.symptomtracker.core.domain

import com.example.symptomtracker.core.domain.usecase.MealieCredentialsValidation
import com.example.symptomtracker.core.domain.usecase.ValidateMealieCredentialsUseCase
import com.example.symptomtracker.core.network.MealieService
import com.example.symptomtracker.core.network.MealieServiceFactory
import com.example.symptomtracker.core.network.model.MealieUser
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.HttpURLConnection

class ValidateMealieCredentialsUseCaseTest {
    private var mockWebServer: MockWebServer = MockWebServer()
    private lateinit var mealieServiceFactory: MealieServiceFactory
    private lateinit var validateMealieCredentialsUseCase: ValidateMealieCredentialsUseCase

    @Before
    fun setup() {
        mockWebServer.start()
        mealieServiceFactory = object : MealieServiceFactory {
            override fun create(baseUrl: String, apiToken: String): MealieService {
                return Retrofit.Builder().baseUrl(mockWebServer.url("/"))
                    .addConverterFactory(GsonConverterFactory.create()).build()
                    .create(MealieService::class.java)
            }
        }

        validateMealieCredentialsUseCase = ValidateMealieCredentialsUseCase(mealieServiceFactory)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun whenUserIsAuthenticated_aSuccessResultIsReturned() = runBlocking {
        val mockResponse = MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""{"username": "testUser"}""")
        mockWebServer.enqueue(mockResponse)

        val result = validateMealieCredentialsUseCase.invoke("http://test.com", "apiToken")

        assertEquals(MealieCredentialsValidation.Success("testUser"), result)
    }

    @Test
    fun whenResponseIsNotSuccessful_anErrorResultIsReturned() = runBlocking {
        val mockResponse = MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        mockWebServer.enqueue(mockResponse)

        val result = validateMealieCredentialsUseCase.invoke("http://test.com", "apiToken")

        assertEquals(MealieCredentialsValidation.Error, result)
    }

    @Test
    fun whenExceptionIsThrown_anErrorResultIsReturned() = runBlocking {
        // Create mocks for testing the exception handling
        val mockMealieServiceFactory: MealieServiceFactory = mock(MealieServiceFactory::class.java)
        val mockMealieService: MealieService = mock(MealieService::class.java)
        val mockCall: Call<MealieUser> = mock(Call::class.java) as Call<MealieUser>

        `when`(mockMealieServiceFactory.create("http://test.com", "apiToken")).thenReturn(
            mockMealieService
        )
        `when`(mockMealieService.getAuthenticatedUser()).thenReturn(mockCall)
        `when`(mockCall.execute()).thenThrow(IOException("Failure"))

        val useCaseWithMockedService = ValidateMealieCredentialsUseCase(mockMealieServiceFactory)
        val result = useCaseWithMockedService.invoke("http://test.com", "apiToken")

        assertEquals(MealieCredentialsValidation.Error, result)
    }
}
