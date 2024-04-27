package com.example.symptomtracker.data.di

import com.example.symptomtracker.data.AppDatabase
import com.example.symptomtracker.data.food.FoodLogDao
import com.example.symptomtracker.data.movement.MovementDao
import com.example.symptomtracker.data.symptom.SymptomDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesFoodLogDao(appDatabase: AppDatabase): FoodLogDao {
        return appDatabase.foodLogDao()
    }

    @Provides
    fun providesSymptomDao(appDatabase: AppDatabase): SymptomDao {
        return appDatabase.symptomDao()
    }

    @Provides
    fun providesMovementDao(appDatabase: AppDatabase): MovementDao {
        return appDatabase.movementDao()
    }
}
