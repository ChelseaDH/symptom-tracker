package com.example.symptomtracker.core.ui

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
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.Calendar

data class DateInputFields(
    val year: Int,
    val month: Int,
    val day: Int,
)

fun DateInputFields.toDisplayString(): String {
    return "%02d/%02d/%d".format(day, month + 1, year)
}

data class TimeInputFields(
    val hour: Int,
    val minute: Int,
)

fun TimeInputFields.toDisplayString(): String {
    return "%02d:%02d".format(hour, minute)
}

data class DateTimeInput(
    val dateInputFields: DateInputFields,
    val timeInputFields: TimeInputFields,
) {
    constructor(calendar: Calendar) : this(
        dateInputFields = DateInputFields(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH),
            day = calendar.get(Calendar.DAY_OF_MONTH),
        ),
        timeInputFields = TimeInputFields(
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE),
        ),
    )
}

fun DateTimeInput.toDate(): OffsetDateTime {
    val calendar = Calendar.getInstance()
    calendar.set(
        dateInputFields.year,
        dateInputFields.month,
        dateInputFields.day,
        timeInputFields.hour,
        timeInputFields.minute
    )

    return calendar.time.toInstant().atOffset(ZoneId.systemDefault().rules.getOffset(Instant.now()))
}

@Composable
fun DateInput(
    dateInputFields: DateInputFields,
    onDateChanged: (DateInputFields) -> Unit,
    labelOnTextField: Boolean,
    modifier: Modifier = Modifier,
    labelSpacing: Dp = 4.dp,
    date: OffsetDateTime,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    if (showDatePicker) {
        DatePickerModal(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = {
                val selectedDate =
                    OffsetDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                onDateChanged(
                    DateInputFields(
                        selectedDate.year,
                        selectedDate.monthValue - 1,
                        selectedDate.dayOfMonth
                    )
                )
            },
            initialDate = date,
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
            value = dateInputFields.toDisplayString(),
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
            }
        )
    }
}

@Composable
fun TimeInput(
    timeInput: TimeInputFields,
    onTimeChanged: (TimeInputFields) -> Unit,
    labelOnTextField: Boolean,
    modifier: Modifier = Modifier,
    labelSpacing: Dp = 4.dp,
    date: OffsetDateTime,
) {
    var showTimePicker by remember { mutableStateOf(false) }
    if (showTimePicker) {
        TimePickerModal(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { hour, min ->
                onTimeChanged(TimeInputFields(hour, min))
            },
            initialTime = date,
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
            value = timeInput.toDisplayString(),
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
            }
        )
    }
}

@Composable
fun DateTimeInputRow(
    dateTimeInput: DateTimeInput,
    onDateChanged: (DateInputFields) -> Unit,
    onTimeChanged: (TimeInputFields) -> Unit,
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
                dateInputFields = dateTimeInput.dateInputFields,
                onDateChanged = onDateChanged,
                labelOnTextField = labelOnTextField,
                labelSpacing = labelSpacing,
                date = dateTimeInput.toDate(),
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            TimeInput(
                timeInput = dateTimeInput.timeInputFields,
                onTimeChanged = onTimeChanged,
                labelOnTextField = labelOnTextField,
                labelSpacing = labelSpacing,
                date = dateTimeInput.toDate(),
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
                    color = containerColor
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
    onDismissRequest: () -> Unit,
    onDateSelected: (Long) -> Unit,
    initialDate: OffsetDateTime,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toInstant().toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let(onDateSelected)
                onDismissRequest()
            }) {
                Text(text = "OK")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    onDismissRequest: () -> Unit,
    onTimeSelected: (hour: Int, min: Int) -> Unit,
    initialTime: OffsetDateTime,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
    )

    TimePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = {
                onTimeSelected(timePickerState.hour, timePickerState.minute)
                onDismissRequest()
            }) {
                Text(text = "OK")
            }
        }
    ) {
        TimePicker(state = timePickerState)
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    SymptomTrackerTheme {
        val dateInputFields = DateInputFields(
            year = 2023,
            month = 5,
            day = 22,
        )
        val timeInputFields = TimeInputFields(
            hour = 14,
            minute = 2,
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DateInput(
                dateInputFields = dateInputFields,
                onDateChanged = {},
                labelOnTextField = true,
                date = OffsetDateTime.parse("2023-03-02T00:00:00+00:00"),
            )
            DateInput(
                dateInputFields = dateInputFields,
                onDateChanged = {},
                labelOnTextField = false,
                date = OffsetDateTime.parse("2023-03-02T00:00:00+00:00"),
            )
            TimeInput(
                timeInput = timeInputFields, onTimeChanged = {}, labelOnTextField = true,
                date = OffsetDateTime.parse("2023-03-02T00:00:00+00:00")
            )
            TimeInput(
                timeInput = timeInputFields, onTimeChanged = {}, labelOnTextField = false,
                date = OffsetDateTime.parse("2023-03-02T00:00:00+00:00")
            )
            DateTimeInputRow(
                dateTimeInput = DateTimeInput(dateInputFields, timeInputFields),
                onDateChanged = {},
                onTimeChanged = {},
                labelOnTextField = false
            )
        }
    }
}
