package com.example.symptomtracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun NoLogsFoundCard(modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_list_24),
                contentDescription = stringResource(R.string.no_logs_found)
            )
            Text(
                text = stringResource(R.string.no_logs_found),
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}

@Composable
fun LogItemCard(
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    title: String,
    date: OffsetDateTime,
    dateTimeFormatter: DateTimeFormatter,
    supportingText: String,
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                icon?.invoke()
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = date.format(dateTimeFormatter),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(start = if (icon !== null) 28.dp else 0.dp)
            )
        }
    }
}

@Preview
@Composable
fun NoLogsFoundPreview() {
    NoLogsFoundCard()
}

@Preview
@Composable
fun LogItemCardPreview() {
    LogItemCard(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_nutrition_24),
                contentDescription = "Food"
            )
        },
        title = "Food",
        date = OffsetDateTime.parse("2023-03-02T13:15:00+00:00"),
        dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss"),
        supportingText = "banana, oats, yoghurt"
    )
}
