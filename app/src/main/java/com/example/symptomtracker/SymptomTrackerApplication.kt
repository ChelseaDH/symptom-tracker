package com.example.symptomtracker

import android.app.Application
import com.example.symptomtracker.data.AppContainer
import com.example.symptomtracker.data.AppDataContainer

class SymptomTrackerApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}