package com.example.symptomtracker.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.core.designsystem.success

@Composable
fun ErrorCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    ResultCard(
        message = message,
        color = MaterialTheme.colorScheme.error,
        painterResId = R.drawable.outline_warning_24,
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
        painterResId = R.drawable.outline_check_circle_24,
        modifier = modifier
    )
}

@Composable
private fun ResultCard(
    message: String,
    color: Color,
    @DrawableRes painterResId: Int,
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
                painter = painterResource(id = painterResId),
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
