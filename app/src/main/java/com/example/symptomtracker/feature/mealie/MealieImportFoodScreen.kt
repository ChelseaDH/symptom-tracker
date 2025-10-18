package com.example.symptomtracker.feature.mealie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.core.designsystem.component.ErrorCard
import com.example.symptomtracker.core.designsystem.component.LabelledOutlinedTextInputField
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.designsystem.icon.ClearIcon
import com.example.symptomtracker.core.designsystem.icon.DeleteIcon
import com.example.symptomtracker.core.domain.model.Ingredient
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealieImportRoute(
    navigateBack: () -> Unit,
    navigateToAddFood: (List<String>, LocalDate?) -> Unit,
    viewModel: MealieImportFoodViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = {
        SymptomTrackerTopAppBar(
            title = stringResource(R.string.mealie_import_title),
            navigateUp = navigateBack,
            actions = {
                TextButton(
                    onClick = {
                        navigateToAddFood(
                            viewModel.getIngredientNames(),
                            viewModel.date,
                        )
                    },
                    enabled = uiState.canImport,
                ) {
                    Text(text = stringResource(R.string.action_next))
                }
            })
    }) { innerPadding ->
        MealieImportBody(
            uiState = uiState,
            eventSink = viewModel::handleEvent,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
internal fun MealieImportBody(
    uiState: MealieImportFoodState,
    eventSink: (MealieImportFoodEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Search(
            url = uiState.url,
            onUrlUpdated = { eventSink(MealieImportFoodEvent.UpdateUrl(it)) },
            onUrlCleared = { eventSink(MealieImportFoodEvent.ClearUrl) },
            onSearch = { eventSink(MealieImportFoodEvent.Search) },
        )

        HorizontalDivider()
        when (uiState.searchState) {
            null -> {}

            MealieImportSearchState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )

            is MealieImportSearchState.Success -> SuccessContent(
                ingredients = uiState.searchState.ingredientsToImport,
                onRemoveIngredient = { eventSink(MealieImportFoodEvent.RemoveIngredientFromImport(it)) },
            )

            is MealieImportSearchState.Error -> ErrorContent(error = uiState.searchState)
        }
    }
}

@Composable
internal fun Search(
    url: TextInput,
    onUrlUpdated: (String) -> Unit,
    onUrlCleared: () -> Unit,
    onSearch: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val onSearchTriggered = {
        focusManager.clearFocus()
        onSearch()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LabelledOutlinedTextInputField(
            label = stringResource(R.string.mealie_import_url_label),
            input = url,
            onValueChange = onUrlUpdated,
            trailingIcon = {
                IconButton(onClick = onUrlCleared) {
                    ClearIcon(
                        contentDescription = stringResource(R.string.action_clear_search)
                    )
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchTriggered() })
        )

        FilledTonalButton(
            onClick = { onSearchTriggered() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(text = stringResource(R.string.action_search))
        }
    }
}

@Composable
fun SuccessContent(
    ingredients: List<Ingredient>,
    onRemoveIngredient: (Ingredient) -> Unit,
) {
    Column {
        Text(
            text = stringResource(id = R.string.mealie_import_results_header),
            fontWeight = FontWeight.W500
        )
        LazyColumn {
            items(ingredients) { ingredient ->
                ListItem(
                    headlineContent = { Text(text = ingredient.name) },
                    trailingContent = {
                        IconButton(onClick = { onRemoveIngredient(ingredient) }) {
                            DeleteIcon(
                                contentDescription = stringResource(
                                    R.string.mealie_import_remove_ingreditent_cd,
                                    ingredient.name,
                                )
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
internal fun ErrorContent(error: MealieImportSearchState.Error) {
    ErrorCard(
        message = when (error) {
            is MealieImportSearchState.Error.NoIngredients -> stringResource(id = R.string.mealie_import_error_no_ingredients)
            is MealieImportSearchState.Error.ApiFailure -> if (error.message.isNullOrEmpty()) stringResource(
                id = R.string.mealie_import_error_api_failure
            ) else stringResource(id = R.string.mealie_import_error_api_failure_with_message).format(
                error.message
            )
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(showBackground = true)
@Composable
internal fun MealieImportBodyPreview(@PreviewParameter(MealieImportFoodStateProvider::class) uiState: MealieImportFoodState) {
    SymptomTrackerTheme {
        MealieImportBody(
            uiState = uiState,
            eventSink = {},
        )
    }
}
