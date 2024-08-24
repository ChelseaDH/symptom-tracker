package com.example.symptomtracker.feature.mealie

import android.webkit.URLUtil
import com.example.symptomtracker.core.data.model.MealieSettings
import com.example.symptomtracker.core.domain.MealieCredentialsValidation
import com.example.symptomtracker.core.domain.ValidateMealieCredentialsUseCase
import com.example.symptomtracker.core.model.TextInput
import com.example.symptomtracker.core.model.TextValidationError
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
    fun whenUpdateIsEnabledIsCalled_stateUpdatesAccordingly() = runTest {
        viewModel.uiState.value = MealieSettingsUiState.Success(
            isEnabled = true,
            baseUrl = TextInput(value = "baseUrl"),
            apiToken = TextInput(value = "apiToken"),
        )

        viewModel.updateIsEnabled(false)

        assertEquals(
            MealieSettingsUiState.Success(
                isEnabled = false,
                baseUrl = TextInput(value = "baseUrl"),
                apiToken = TextInput(value = "apiToken"),
                credentialsCheckResult = null,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun whenUpdateBaseUrlIsCalled_stateUpdatesAccordingly() = runTest {
        viewModel.uiState.value = MealieSettingsUiState.Success(
            isEnabled = true,
            baseUrl = TextInput(value = "baseUrl"),
            apiToken = TextInput(value = "apiToken"),
        )

        viewModel.updateBaseUrl("newUrl")

        assertEquals(
            MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = "newUrl"),
                apiToken = TextInput(value = "apiToken"),
                credentialsCheckResult = null,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun whenUpdateApiTokenIsCalled_stateUpdatesAccordingly() = runTest {
        viewModel.uiState.value = MealieSettingsUiState.Success(
            isEnabled = true,
            baseUrl = TextInput(value = "baseUrl"),
            apiToken = TextInput(value = "apiToken"),
        )

        viewModel.updateApiToken("newToken")

        assertEquals(
            MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = "baseUrl"),
                apiToken = TextInput(value = "newToken"),
                credentialsCheckResult = null,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun givenInputsAreInvalid_whenOnCheckCredentialsIsCalled_stateUpdatesWithApplicableValidationErrors() =
        mapOf(
            MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = ""), apiToken = TextInput(value = ""),
            ) to MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = "", validationError = TextValidationError.BLANK),
                apiToken = TextInput(value = "", validationError = TextValidationError.BLANK),
                credentialsCheckResult = null,
            ), MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = "notAUrl"), apiToken = TextInput(value = "apiToken"),
            ) to MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(
                    value = "notAUrl", validationError = TextValidationError.INVALID
                ),
                apiToken = TextInput(value = "apiToken", validationError = null),
                credentialsCheckResult = null,
            )
        ).forEach { (stateBefore, stateAfter) ->
            runTest {
                viewModel.uiState.value = stateBefore

                mockStatic(URLUtil::class.java).use { mock ->
                    mock.`when`<Boolean> { URLUtil.isValidUrl("notAUrl") }.thenReturn(false)

                    viewModel.onCheckCredentials()

                    assertEquals(stateAfter, viewModel.uiState.value)
                }
            }
        }

    @Test
    fun givenCredentialsAreNotValid_whenonCheckCredentialsIsCalled_stateUpdatesWithApplicableValidationErrors() =
        runTest {
            viewModel.uiState.value = MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = "https://test.mealie.co"),
                apiToken = TextInput(value = "apiToken"),
            )

            mockStatic(URLUtil::class.java).use {
                it.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co") }.thenReturn(true)

                val validationResponse = MealieCredentialsValidation.Success(username = "Test User")
                `when`(
                    validateMealieCredentialsUseCase.invoke(
                        "https://test.mealie.co", "apiToken"
                    )
                ).thenReturn(validationResponse)

                viewModel.onCheckCredentials()

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
        }

    @Test
    fun givenStateIsLoading_whenOnSaveIsCalled_saveResultIsFalse() = runTest {
        viewModel.uiState.value = MealieSettingsUiState.Loading

        viewModel.onSave()

        assertFalse(viewModel.saveResult.value!!)
    }

    @Test
    fun givenInputsAreInvalid_whenOnSaveIsCalled_saveResultIsFalse() = runTest {
        viewModel.uiState.value = MealieSettingsUiState.Success(
            isEnabled = true,
            baseUrl = TextInput(value = "https://test.mealie.co"),
            apiToken = TextInput(value = "apiToken"),
        )

        mockStatic(URLUtil::class.java).use {
            it.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co") }.thenReturn(true)

            val validationResponse = MealieCredentialsValidation.Error
            `when`(
                validateMealieCredentialsUseCase.invoke(
                    "https://test.mealie.co", "apiToken"
                )
            ).thenReturn(validationResponse)

            viewModel.onSave()

            assertFalse(viewModel.saveResult.value!!)
        }
    }

    @Test
    fun givenIsEnabledIsFalse_whenOnSaveIsCalled_saveResultIsTrue() = runTest {
        viewModel.uiState.value = MealieSettingsUiState.Success(
            isEnabled = false,
            baseUrl = TextInput(value = ""),
            apiToken = TextInput(value = "apiToken"),
        )

        viewModel.onSave()

        assertTrue(viewModel.saveResult.value!!)
    }

    @Test
    fun givenInputsAreValid_whenOnSaveIsCalled_saveResultIsTrue() = runTest {
        viewModel.uiState.value = MealieSettingsUiState.Success(
            isEnabled = true,
            baseUrl = TextInput(value = "https://test.mealie.co"),
            apiToken = TextInput(value = "apiToken"),
        )

        mockStatic(URLUtil::class.java).use {
            it.`when`<Boolean> { URLUtil.isValidUrl("https://test.mealie.co") }.thenReturn(true)

            val validationResponse = MealieCredentialsValidation.Success(username = "Test User")
            `when`(
                validateMealieCredentialsUseCase.invoke(
                    "https://test.mealie.co", "apiToken"
                )
            ).thenReturn(validationResponse)

            viewModel.onSave()

            assertTrue(viewModel.saveResult.value!!)
        }
    }
}
