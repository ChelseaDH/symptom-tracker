package com.example.symptomtracker.feature.settings

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.symptomtracker.core.database.util.DatabaseBackup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class DatabaseBackupViewModel @Inject constructor(private val databaseBackup: DatabaseBackup) :
    ViewModel() {
    val uiState = MutableStateFlow(DatabaseBackupUiState())

    fun exportDatabase() {
        uiState.value = DatabaseBackupUiState(loading = true, result = null)

        try {
            databaseBackup.downloadDatabase()
            uiState.value = DatabaseBackupUiState(loading = false, result = Result.Success)
        } catch (e: Exception) {
            Log.d("databaseBackUp", "exportDatabase: $e")
            uiState.value = DatabaseBackupUiState(loading = false, result = Result.Error(e))
        }
    }

    fun importDatabase(uri: Uri) {
        uiState.value = DatabaseBackupUiState(loading = false, result = Result.Success)
        Log.d("databaseBackUp", "importDatabase: $uri")

        try {
            databaseBackup.restoreDatabase(uri)
            uiState.value = DatabaseBackupUiState(loading = false, result = Result.Success)
        } catch (e: Exception) {
            uiState.value = DatabaseBackupUiState(loading = false, result = Result.Error(e))
        }
    }

    fun resetUiState() {
        uiState.value = DatabaseBackupUiState()
    }
}

data class DatabaseBackupUiState(
    val loading: Boolean = false,
    val result: Result? = null,
)

sealed interface Result {
    data object Success : Result
    data class Error(val exception: Exception) : Result
}
