package com.example.symptomtracker.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.symptomtracker.R
import com.example.symptomtracker.core.ui.MealieIcon

@Composable
fun SettingsScreen(
    navigateToManageFoodItems: () -> Unit,
    navigateToMealieSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.manage_food_items_title))
                },
                modifier = Modifier.clickable { navigateToManageFoodItems() },
                leadingContent = {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.outline_nutrition_24
                        ),
                        contentDescription = stringResource(id = R.string.manage_food_items_title)
                    )
                }
            )

            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.mealie_settings_title))
                },
                modifier = Modifier.clickable { navigateToMealieSettings() },
                leadingContent = { MealieIcon() }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ConfigScreenPreview() {
    SettingsScreen(navigateToManageFoodItems = { }, navigateToMealieSettings = {})
}
