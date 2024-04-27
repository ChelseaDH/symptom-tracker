package com.example.symptomtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.symptomtracker.ui.rememberAppState
import com.example.symptomtracker.ui.theme.SymptomTrackerTheme
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
