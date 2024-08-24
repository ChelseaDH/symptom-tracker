package com.example.symptomtracker.core.network

import com.example.symptomtracker.core.util.ensureTrailingSlash
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface MealieServiceFactory {
    fun create(baseUrl: String, apiToken: String): MealieService
}

class DefaultMealieServiceFactory : MealieServiceFactory {
    override fun create(baseUrl: String, apiToken: String): MealieService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiToken").build()

                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl.ensureTrailingSlash())
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(MealieService::class.java)
    }
}
