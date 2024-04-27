package com.example.symptomtracker.data.di

import com.example.symptomtracker.data.food.FoodLogRepository
import com.example.symptomtracker.data.food.OfflineFoodLogRepository
import com.example.symptomtracker.data.movement.MovementRepository
import com.example.symptomtracker.data.movement.OfflineMovementRepository
import com.example.symptomtracker.data.symptom.OfflineSymptomRepository
import com.example.symptomtracker.data.symptom.SymptomRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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
}
