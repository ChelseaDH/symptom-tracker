package com.example.symptomtracker.core.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R

@Composable
fun MealieIcon() {
    Icon(
        painter = painterResource(id = R.drawable.mealie_x64),
        contentDescription = stringResource(id = R.string.mealie_settings_title),
        modifier = Modifier.size(24.dp)
    )
}
