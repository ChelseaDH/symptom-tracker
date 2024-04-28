package com.example.symptomtracker.core.data.di

import com.example.symptomtracker.core.data.repository.FoodLogRepository
import com.example.symptomtracker.core.data.repository.MovementRepository
import com.example.symptomtracker.core.data.repository.OfflineFoodLogRepository
import com.example.symptomtracker.core.data.repository.OfflineMovementRepository
import com.example.symptomtracker.core.data.repository.OfflineSymptomRepository
import com.example.symptomtracker.core.data.repository.SymptomRepository
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
