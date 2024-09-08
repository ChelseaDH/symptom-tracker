package com.example.symptomtracker.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset

data class DateTimeInput(
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
) {
    constructor(date: OffsetDateTime) : this(
        date = date.toLocalDate(),
        time = date.toLocalTime(),
    )

    fun toDate(): OffsetDateTime =
        OffsetDateTime.of(date, time, ZoneId.systemDefault().rules.getOffset(Instant.now()))
}

@Composable
fun DateInput(
    date: LocalDate,
    onDateChanged: (LocalDate) -> Unit,
    labelOnTextField: Boolean,
    modifier: Modifier = Modifier,
    labelSpacing: Dp = 4.dp,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    if (showDatePicker) {
        DatePickerModal(
            date = date,
            onDismissRequest = { showDatePicker = false },
            onDateSelected = onDateChanged,
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(labelSpacing),
    ) {
        if (!labelOnTextField) {
            Text(text = stringResource(R.string.date_label))
        }
        OutlinedTextField(
            value = date.toDisplayString(),
            onValueChange = {},
            label = {
                if (labelOnTextField) {
                    Text(
                        text = stringResource(R.string.date_label),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            },
            modifier = modifier,
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_date_cd)
                    )
                }
            },
        )
    }
}

@Composable
fun TimeInput(
    time: LocalTime,
    onTimeChanged: (LocalTime) -> Unit,
    labelOnTextField: Boolean,
    modifier: Modifier = Modifier,
    labelSpacing: Dp = 4.dp,
) {
    var showTimePicker by remember { mutableStateOf(false) }
    if (showTimePicker) {
        TimePickerModal(
            time = time,
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = onTimeChanged,
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(labelSpacing),
    ) {
        if (!labelOnTextField) {
            Text(text = stringResource(R.string.time_label))
        }
        OutlinedTextField(
            value = time.toDisplayString(),
            onValueChange = {},
            label = {
                if (labelOnTextField) {
                    Text(
                        text = stringResource(R.string.time_label),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            },
            modifier = modifier,
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_time_cd)
                    )
                }
            },
        )
    }
}

@Composable
fun DateTimeInputRow(
    dateTimeInput: DateTimeInput,
    onDateChanged: (LocalDate) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    labelOnTextField: Boolean,
    modifier: Modifier = Modifier,
    labelSpacing: Dp = 4.dp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.weight(2f)) {
            DateInput(
                date = dateTimeInput.date,
                onDateChanged = onDateChanged,
                labelOnTextField = labelOnTextField,
                labelSpacing = labelSpacing,
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            TimeInput(
                time = dateTimeInput.time,
                onTimeChanged = onTimeChanged,
                labelOnTextField = labelOnTextField,
                labelSpacing = labelSpacing,
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = containerColor,
                ),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    date: LocalDate,
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant()
                .toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= System.currentTimeMillis()
                }
            },
        )

    DatePickerDialog(onDismissRequest = onDismissRequest, confirmButton = {
        Button(onClick = {
            datePickerState.selectedDateMillis?.let {
                onDateSelected(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
            }
            onDismissRequest()
        }) {
            Text(text = "OK")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

fun LocalDate.toDisplayString(): String {
    return "%02d/%02d/%d".format(dayOfMonth, monthValue, year)
}

fun LocalTime.toDisplayString(): String {
    return "%02d:%02d".format(hour, minute)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    time: LocalTime,
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = time.hour,
        initialMinute = time.minute,
        is24Hour = true,
    )

    TimePickerDialog(onDismissRequest = onDismissRequest, confirmButton = {
        Button(onClick = {
            onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
            onDismissRequest()
        }) {
            Text(text = "OK")
        }
    }) {
        TimePicker(state = timePickerState)
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    SymptomTrackerTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DateInput(
                date = LocalDate.now(),
                onDateChanged = {},
                labelOnTextField = true,
            )
            DateInput(
                date = LocalDate.now(),
                onDateChanged = {},
                labelOnTextField = false,
            )
            TimeInput(
                time = LocalTime.now(),
                onTimeChanged = {},
                labelOnTextField = true,
            )
            TimeInput(
                time = LocalTime.now(),
                onTimeChanged = {},
                labelOnTextField = false,
            )
            DateTimeInputRow(
                dateTimeInput = DateTimeInput(LocalDate.now(), LocalTime.now()),
                onDateChanged = {},
                onTimeChanged = {},
                labelOnTextField = false
            )
        }
    }
}
