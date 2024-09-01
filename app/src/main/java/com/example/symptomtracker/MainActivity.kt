package com.example.symptomtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.ui.SymptomTrackerApp
import com.example.symptomtracker.ui.rememberAppState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val appState = rememberAppState()

            SymptomTrackerTheme {
                SymptomTrackerApp(appState = appState)
            }
        }
    }
}
