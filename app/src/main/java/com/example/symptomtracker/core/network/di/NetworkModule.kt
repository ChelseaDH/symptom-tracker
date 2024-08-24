package com.example.symptomtracker.core.network.di

import com.example.symptomtracker.core.data.repository.SettingsRepository
import com.example.symptomtracker.core.network.DefaultMealieServiceFactory
import com.example.symptomtracker.core.network.MealieService
import com.example.symptomtracker.core.network.MealieServiceFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideMealieService(settingsRepository: SettingsRepository): MealieService {
        return runBlocking {
            settingsRepository.getMealieSettings().map { settings ->
                DefaultMealieServiceFactory().create(
                    baseUrl = settings.baseUrl,
                    apiToken = settings.apiToken,
                )
            }.first()
        }
    }

    @Provides
    @Singleton
    fun provideMealieServiceFactory(): MealieServiceFactory {
        return DefaultMealieServiceFactory()
    }
}
