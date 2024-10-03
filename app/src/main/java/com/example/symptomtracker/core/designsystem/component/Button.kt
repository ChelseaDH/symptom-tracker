package com.example.symptomtracker.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme

@Composable
fun FilledTonalButtonWithIcon(
    @StringRes textId: Int,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
    ) {
        Row(
            modifier = Modifier.sizeIn(minWidth = 80.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = stringResource(id = textId))
        }
    }
}

@Preview
@Composable
fun FilledTonalButtonWithIconPreview() {
    SymptomTrackerTheme {
        FilledTonalButtonWithIcon(
            textId = R.string.app_name,
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                )
            },
            onClick = {},
        )
    }
}
