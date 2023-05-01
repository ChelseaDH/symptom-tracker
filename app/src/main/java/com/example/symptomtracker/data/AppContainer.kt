package com.example.symptomtracker.data

import android.content.Context
import com.example.symptomtracker.data.food.FoodLogRepository
import com.example.symptomtracker.data.food.OfflineFoodLogRepository
import com.example.symptomtracker.data.symptom.OfflineSymptomRepository
import com.example.symptomtracker.data.symptom.SymptomRepository

/**
 * App container for dependency injection.
 */
interface AppContainer {
    val symptomRepository: SymptomRepository
    val foodLogRepository: FoodLogRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineSymptomRepository] and [OfflineFoodLogRepository].
 */
class AppDataContainer(context: Context) : AppContainer {
    private val database = AppDatabase.getDatabase(context)

    override val symptomRepository: SymptomRepository by lazy {
        OfflineSymptomRepository(database.symptomDao())
    }

    override val foodLogRepository: FoodLogRepository by lazy {
        OfflineFoodLogRepository(database.foodLogDao())
    }
}