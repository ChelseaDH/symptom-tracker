package com.example.symptomtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.symptomtracker.ui.theme.SymptomTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SymptomTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    SymptomTrackerApp()
                }
            }
        }
    }
}
