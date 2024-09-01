package com.example.symptomtracker.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.symptomtracker.core.data.repository.OfflineFoodLogRepository
import com.example.symptomtracker.core.data.repository.OfflineMovementRepository
import com.example.symptomtracker.core.data.repository.OfflineSettingsRepository
import com.example.symptomtracker.core.data.repository.OfflineSymptomRepository
import com.example.symptomtracker.core.domain.repository.FoodLogRepository
import com.example.symptomtracker.core.domain.repository.MovementRepository
import com.example.symptomtracker.core.domain.repository.SettingsRepository
import com.example.symptomtracker.core.domain.repository.SymptomRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindsFoodLogRepository(
        foodLogRepository: OfflineFoodLogRepository
    ): FoodLogRepository

    @Binds
    internal abstract fun bindsSymptomLogRepository(
        symptomLogRepository: OfflineSymptomRepository
    ): SymptomRepository

    @Binds
    internal abstract fun bindsMovementLogRepository(
        movementLogRepository: OfflineMovementRepository
    ): MovementRepository

    @Binds
    internal abstract fun bindsSettingsRepository(
        settingsRepository: OfflineSettingsRepository
    ): SettingsRepository

    companion object {
        @Provides
        @Singleton
        fun providesSettingsPreferences(@ApplicationContext applicationContext: Context): DataStore<Preferences> {
            return applicationContext.settingsDataStore
        }
    }
}
