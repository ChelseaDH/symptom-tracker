package com.example.symptomtracker.data

import android.content.Context
import com.example.symptomtracker.data.symptom.OfflineSymptomRepository
import com.example.symptomtracker.data.symptom.SymptomRepository

/**
 * App container for dependency injection.
 */
interface AppContainer {
    val symptomRepository: SymptomRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineSymptomRepository].
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val symptomRepository: SymptomRepository by lazy {
        OfflineSymptomRepository(AppDatabase.getDatabase(context).symptomDao())
    }
}