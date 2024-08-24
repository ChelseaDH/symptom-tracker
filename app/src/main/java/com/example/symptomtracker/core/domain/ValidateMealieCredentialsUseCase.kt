package com.example.symptomtracker.core.domain

import com.example.symptomtracker.core.network.MealieServiceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ValidateMealieCredentialsUseCase @Inject constructor(private val mealieServiceFactory: MealieServiceFactory) {
    suspend operator fun invoke(baseUrl: String, apiToken: String): MealieCredentialsValidation {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    mealieServiceFactory.create(baseUrl = baseUrl, apiToken = apiToken)
                        .getAuthenticatedUser()
                        .execute()

                if (response.isSuccessful) MealieCredentialsValidation.Success(response.body()!!.username)
                else MealieCredentialsValidation.Error
            } catch (t: Throwable) {
                MealieCredentialsValidation.Error
            }
        }
    }
}

sealed interface MealieCredentialsValidation {
    data object Error : MealieCredentialsValidation
    data class Success(val username: String) : MealieCredentialsValidation
}
