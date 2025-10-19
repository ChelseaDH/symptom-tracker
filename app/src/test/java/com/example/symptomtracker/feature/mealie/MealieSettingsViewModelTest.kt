package com.example.symptomtracker.feature.mealie

import android.webkit.URLUtil
import com.example.symptomtracker.core.data.model.MealieSettings
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.designsystem.component.TextValidationError
import com.example.symptomtracker.core.domain.usecase.MealieCredentialsValidation
import com.example.symptomtracker.core.domain.usecase.ValidateMealieCredentialsUseCase
import com.example.symptomtracker.core.testing.repository.TestSettingsRepository
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class MealieSettingsViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val settingsRepository = TestSettingsRepository()
    private val validateMealieCredentialsUseCase =
        mock(ValidateMealieCredentialsUseCase::class.java)
    private lateinit var viewModel: MealieSettingsViewModel

    @Before
    fun setup() {
        viewModel = MealieSettingsViewModel(
            settingsRepository = settingsRepository,
            validateMealieCredentials = validateMealieCredentialsUseCase,
        )
    }

    @Test
    fun whenInitialised_stateIsLoadingBeforeSettingsAreRetrieved() =
        assertEquals(MealieSettingsUiState.Loading, viewModel.uiState.value)

    @Test
    fun whenInitialised_stateIsSuccessAfterSettingsAreRetrieved() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        settingsRepository.sendMealieSettings(
            MealieSettings(enabled = true, baseUrl = "baseUrl", apiToken = "apiToken")
        )

        assertEquals(
            MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = "baseUrl"),
                apiToken = TextInput(value = "apiToken"),
            ), viewModel.uiState.value
        )

        collectJob.cancel()
    }

    @Test
    fun whenInitialised_saveResultIsNull() = assertNull(viewModel.saveResult.value)

    @Test
    fun updateFunctions_whenCalled_updateStateAsExpected() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        settingsRepository.sendMealieSettings(
            MealieSettings(enabled = true, baseUrl = "baseUrl", apiToken = "apiToken")
        )

        assertEquals(
            MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = "baseUrl"),
                apiToken = TextInput(value = "apiToken"),
            ), viewModel.uiState.value
        )

        viewModel.handleEvent(MealieSettingsEvent.UpdateIsEnabled(false))
        assertEquals(
            MealieSettingsUiState.Success(
                isEnabled = false,
                baseUrl = TextInput(value = "baseUrl"),
                apiToken = TextInput(value = "apiToken"),
                credentialsCheckResult = null,
            ),
            viewModel.uiState.value,
        )

        viewModel.handleEvent(MealieSettingsEvent.UpdateBaseUrl("newUrl"))
        assertEquals(
            MealieSettingsUiState.Success(
                isEnabled = false,
                baseUrl = TextInput(value = "newUrl"),
                apiToken = TextInput(value = "apiToken"),
                credentialsCheckResult = null,
            ),
            viewModel.uiState.value,
        )

        viewModel.handleEvent(MealieSettingsEvent.UpdateApiToken("newToken"))
        assertEquals(
            MealieSettingsUiState.Success(
                isEnabled = false,
                baseUrl = TextInput(value = "newUrl"),
                apiToken = TextInput(value = "newToken"),
                credentialsCheckResult = null,
            ),
            viewModel.uiState.value,
        )

        collectJob.cancel()
    }

    @Test
    fun givenInputsAreInvalid_whenOnCheckCredentialsIsCalled_stateUpdatesWithApplicableValidationErrors() =
        runTest {
            val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            // Test case 1: Empty inputs
            settingsRepository.sendMealieSettings(
                MealieSettings(enabled = true, baseUrl = "", apiToken = "")
            )

            viewModel.handleEvent(MealieSettingsEvent.CheckCredentials)

            assertEquals(
                MealieSettingsUiState.Success(
                    isEnabled = true,
                    baseUrl = TextInput(value = "", validationError = TextValidationError.BLANK),
                    apiToken = TextInput(value = "", validationError = TextValidationError.BLANK),
                    credentialsCheckResult = null,
                ),
                viewModel.uiState.value
            )

            // Test case 2: Invalid URL
            settingsRepository.sendMealieSettings(
                MealieSettings(enabled = true, baseUrl = "notAUrl", apiToken = "apiToken")
            )

            mockStatic(URLUtil::class.java).use { mock ->
                mock.`when`<Boolean> { URLUtil.isValidUrl("notAUrl") }.thenReturn(false)

                viewModel.handleEvent(MealieSettingsEvent.CheckCredentials)

                assertEquals(
                    MealieSettingsUiState.Success(
                        isEnabled = true,
                        baseUrl = TextInput(
                            value = "notAUrl", validationError = TextValidationError.INVALID
                        ),
                        apiToken = TextInput(value = "apiToken", validationError = null),
                        credentialsCheckResult = null,
                    ),
                    viewModel.uiState.value
                )
            }

            collectJob.cancel()
        }

    @Test
    fun givenCredentialsAreNotValid_whenonCheckCredentialsIsCalled_stateUpdatesWithApplicableValidationErrors() =
        runTest {
            val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            settingsRepository.sendMealieSettings(
                MealieSettings(
                    enabled = true,
                    baseUrl = "https://test.mealie.co",
                    apiToken = "apiToken"
                )
            )

            mockStatic(URLUtil::class.java).use {
                it.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co") }.thenReturn(true)

                val validationResponse = MealieCredentialsValidation.Success(username = "Test User")
                `when`(
                    validateMealieCredentialsUseCase.invoke(
                        "https://test.mealie.co", "apiToken"
                    )
                ).thenReturn(validationResponse)

                viewModel.handleEvent(MealieSettingsEvent.CheckCredentials)

                assertEquals(
                    MealieSettingsUiState.Success(
                        isEnabled = true,
                        baseUrl = TextInput(
                            value = "https://test.mealie.co", validationError = null
                        ),
                        apiToken = TextInput(value = "apiToken", validationError = null),
                        credentialsCheckResult = validationResponse,
                    ),
                    viewModel.uiState.value,
                )
            }

            collectJob.cancel()
        }

    @Test
    fun givenStateIsLoading_whenOnSaveIsCalled_saveResultIsFalse() = runTest {
        assertEquals(MealieSettingsUiState.Loading, viewModel.uiState.value)

        viewModel.handleEvent(MealieSettingsEvent.Save)

        assertFalse(viewModel.saveResult.value!!)
    }

    @Test
    fun givenInputsAreInvalid_whenOnSaveIsCalled_saveResultIsFalse() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        settingsRepository.sendMealieSettings(
            MealieSettings(
                enabled = true,
                baseUrl = "https://test.mealie.co",
                apiToken = "apiToken"
            )
        )

        mockStatic(URLUtil::class.java).use {
            it.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co") }.thenReturn(true)

            val validationResponse = MealieCredentialsValidation.Error
            `when`(
                validateMealieCredentialsUseCase.invoke(
                    "https://test.mealie.co", "apiToken"
                )
            ).thenReturn(validationResponse)

            viewModel.handleEvent(MealieSettingsEvent.Save)

            assertFalse(viewModel.saveResult.value!!)
        }

        collectJob.cancel()
    }

    @Test
    fun givenIsEnabledIsFalse_whenOnSaveIsCalled_saveResultIsTrue() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        settingsRepository.sendMealieSettings(
            MealieSettings(enabled = true, baseUrl = "", apiToken = "apiToken")
        )

        // Update to disabled state
        viewModel.handleEvent(MealieSettingsEvent.UpdateIsEnabled(false))

        viewModel.handleEvent(MealieSettingsEvent.Save)

        assertTrue(viewModel.saveResult.value!!)

        collectJob.cancel()
    }

    @Test
    fun givenInputsAreValid_whenOnSaveIsCalled_saveResultIsTrue() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        settingsRepository.sendMealieSettings(
            MealieSettings(
                enabled = true,
                baseUrl = "https://test.mealie.co",
                apiToken = "apiToken"
            )
        )

        mockStatic(URLUtil::class.java).use {
            it.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co") }.thenReturn(true)

            val validationResponse = MealieCredentialsValidation.Success(username = "Test User")
            `when`(
                validateMealieCredentialsUseCase.invoke(
                    "https://test.mealie.co", "apiToken"
                )
            ).thenReturn(validationResponse)

            viewModel.handleEvent(MealieSettingsEvent.Save)

            assertTrue(viewModel.saveResult.value!!)
        }

        collectJob.cancel()
    }
}
