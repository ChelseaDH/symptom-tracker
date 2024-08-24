package com.example.symptomtracker.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ErrorCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    ResultCard(
        message = message,
        color = MaterialTheme.colorScheme.error,
        iconVector = Icons.Default.Warning,
        modifier = modifier
    )
}

@Composable
fun SuccessCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    ResultCard(
        message = message,
        color = MaterialTheme.colorScheme.success,
        iconVector = Icons.Default.CheckCircle,
        modifier = modifier
    )
}

@Composable
private fun ResultCard(
    message: String,
    color: Color,
    iconVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier,
        border = BorderStroke(width = 1.dp, color = color)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically),
                tint = color,
            )
            Text(
                text = message,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
internal fun ErrorCardPreview() {
    SymptomTrackerTheme {
        ErrorCard(message = "Bad request")
    }
}

@Preview
@Composable
internal fun SuccessCardPreview() {
    SymptomTrackerTheme {
        SuccessCard(message = "All good!")
    }
}
