package com.example.symptomtracker.core.database.di

import com.example.symptomtracker.core.database.AppDatabase
import com.example.symptomtracker.core.database.dao.DrinkLogDao
import com.example.symptomtracker.core.database.dao.FoodLogDao
import com.example.symptomtracker.core.database.dao.MovementDao
import com.example.symptomtracker.core.database.dao.SymptomDao
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
    fun providesDrinkLogDao(appDatabase: AppDatabase): DrinkLogDao {
        return appDatabase.drinkLogDao()
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
