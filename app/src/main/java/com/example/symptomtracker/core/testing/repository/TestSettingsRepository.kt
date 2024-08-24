package com.example.symptomtracker.core.testing.repository

import com.example.symptomtracker.core.data.model.MealieSettings
import com.example.symptomtracker.core.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class TestSettingsRepository : SettingsRepository {
    private val mealieSettingsFlow: MutableSharedFlow<MealieSettings> =
        MutableSharedFlow(replay = 1)
    private val mealieEnabledFlow: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 1)

    override fun getMealieSettings(): Flow<MealieSettings> = mealieSettingsFlow

    override fun isMealieIntegrationEnabled(): Flow<Boolean> = mealieEnabledFlow

    override suspend fun disableMealieIntegration() {}

    override suspend fun enableMealieIntegration(baseUrl: String, apiToken: String) {}

    /**
     * A test-only API to allow controlling the settings in tests.
     */
    fun sendMealieSettings(settings: MealieSettings) {
        mealieSettingsFlow.tryEmit(settings)
    }

    fun sendMealieEnabled(enabled: Boolean) {
        mealieEnabledFlow.tryEmit(enabled)
    }
}
