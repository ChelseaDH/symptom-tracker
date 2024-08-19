package com.example.symptomtracker.feature.settings

import com.example.symptomtracker.core.database.util.DatabaseBackup
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DatabaseSettingsViewModelTest {
    private var databaseBackup: DatabaseBackup = mockk()
    private lateinit var viewModel: DatabaseBackupViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DatabaseBackupViewModel(databaseBackup)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initializesWithDefaultValues() {
        assertEquals(DatabaseBackupUiState(loading = false, result = null), viewModel.uiState.value)
    }

    @Test
    fun exportDatabase_updatesResultToSuccessWhenDownloadSucceeds() {
        coEvery { databaseBackup.downloadDatabase() } coAnswers {
            // Assert that the loading state is true before the downloadDatabase function is called
            assertEquals(true, viewModel.uiState.value.loading)
        }

        viewModel.exportDatabase()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            DatabaseBackupUiState(loading = false, result = Result.Success),
            viewModel.uiState.value
        )

        // Verify that the downloadDatabase function was called exactly once
        verify(exactly = 1) { databaseBackup.downloadDatabase() }
    }

    @Test
    fun exportDatabase_updatesResultToErrorWhenAnExceptionIsThrownDownloading() {
        val exception = RuntimeException("Failed to create file in Downloads folder")
        coEvery { databaseBackup.downloadDatabase() } throws exception coAndThen {
            // Assert that the loading state is true before the downloadDatabase function is called
            assertEquals(true, viewModel.uiState.value.loading)
        }

        viewModel.exportDatabase()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            DatabaseBackupUiState(loading = false, result = Result.Error(exception)),
            viewModel.uiState.value
        )

        // Verify that the downloadDatabase function was called exactly once
        verify(exactly = 1) { databaseBackup.downloadDatabase() }
    }
}
