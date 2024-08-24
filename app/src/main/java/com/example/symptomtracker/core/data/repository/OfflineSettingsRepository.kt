package com.example.symptomtracker.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.symptomtracker.core.data.model.MealieSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineSettingsRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
    SettingsRepository {
    private companion object {
        val MEALIE_ENABLED = booleanPreferencesKey("mealie_enabled")
        val MEALIE_BASE_URL = stringPreferencesKey("mealie_base_url")
        val MEALIE_API_TOKEN = stringPreferencesKey("mealie_api_token")
    }

    override fun getMealieSettings(): Flow<MealieSettings> = dataStore.data.map { preferences ->
        MealieSettings(
            enabled = preferences[MEALIE_ENABLED] ?: false,
            baseUrl = preferences[MEALIE_BASE_URL].orEmpty(),
            apiToken = preferences[MEALIE_API_TOKEN].orEmpty(),
        )
    }

    override fun isMealieIntegrationEnabled(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[MEALIE_ENABLED] ?: false
    }

    override suspend fun disableMealieIntegration() {
        dataStore.edit { preferences ->
            preferences[MEALIE_ENABLED] = false
        }
    }

    override suspend fun enableMealieIntegration(baseUrl: String, apiToken: String) {
        dataStore.edit { preferences ->
            preferences[MEALIE_ENABLED] = true
            preferences[MEALIE_BASE_URL] = baseUrl
            preferences[MEALIE_API_TOKEN] = apiToken
        }
    }
}
