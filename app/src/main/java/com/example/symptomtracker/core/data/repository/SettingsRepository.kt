package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.data.model.MealieSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getMealieSettings(): Flow<MealieSettings>

    fun isMealieIntegrationEnabled(): Flow<Boolean>

    suspend fun disableMealieIntegration()

    suspend fun enableMealieIntegration(baseUrl: String, apiToken: String)
}
