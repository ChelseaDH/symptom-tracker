package com.example.symptomtracker.core.network

import com.example.symptomtracker.core.network.model.MealieRecipe
import com.example.symptomtracker.core.network.model.MealieUser
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MealieService {
    @GET("api/users/self")
    fun getAuthenticatedUser(): Call<MealieUser>

    @GET("api/recipes/{slug}")
    fun getRecipeBySlug(@Path("slug") slug: String): Call<MealieRecipe>
}
