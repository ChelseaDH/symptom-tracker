package com.example.symptomtracker.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.core.designsystem.icon.EditIcon
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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
fun DateInputField(
    date: LocalDate,
    onDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    LabelledOutlinedTextField(
        label = stringResource(R.string.date_label),
        value = date.toDisplayString(),
        onValueChange = {},
        modifier = modifier,
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                EditIcon(
                    contentDescription = stringResource(R.string.edit_date_cd)
                )
            }
        },
        singleLine = true,
    )

    if (showDatePicker) {
        DatePickerModal(
            date = date,
            onDismissRequest = { showDatePicker = false },
            onDateSelected = onDateChanged,
        )
    }
}

@Composable
fun TimeInputField(
    time: LocalTime,
    onTimeChanged: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTimePicker by remember { mutableStateOf(false) }

    LabelledOutlinedTextField(
        label = stringResource(R.string.time_label),
        value = time.toDisplayString(),
        onValueChange = {},
        modifier = modifier,
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showTimePicker = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_edit_24),
                    contentDescription = stringResource(R.string.edit_time_cd)
                )
            }
        },
        singleLine = true,
    )

    if (showTimePicker) {
        TimePickerModal(
            time = time,
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = onTimeChanged,
        )
    }
}

@Composable
fun DateTimeInputRow(
    dateTimeInput: DateTimeInput,
    onDateChanged: (LocalDate) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DateInputField(
            date = dateTimeInput.date,
            onDateChanged = onDateChanged,
            modifier = Modifier.weight(2f),
        )
        TimeInputField(
            time = dateTimeInput.time,
            onTimeChanged = onTimeChanged,
            modifier = Modifier.weight(1f),
        )
    }
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

    /** Determines whether the time picker is dial or input */
    var showDial by remember { mutableStateOf(true) }
    val toggleIconId = if (showDial) {
        R.drawable.outline_keyboard_24
    } else {
        R.drawable.outline_access_time_24
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = stringResource(R.string.datetime_select_time),
                    style = MaterialTheme.typography.labelLarge
                )

                if (showDial) {
                    TimePicker(state = timePickerState)
                } else {
                    TimeInput(state = timePickerState)
                }

                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    IconButton(onClick = { showDial = !showDial }) {
                        Icon(
                            painter = painterResource(id = toggleIconId),
                            contentDescription = stringResource(R.string.datetime_time_picker_type_toggle),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismissRequest) {
                        Text(text = stringResource(id = R.string.action_cancel))
                    }
                    TextButton(onClick = {
                        onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                        onDismissRequest()
                    }) {
                        Text(text = stringResource(id = R.string.ok_label))
                    }
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

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    onDateSelected(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                }
                onDismissRequest()
            }) {
                Text(text = stringResource(id = R.string.ok_label))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.action_cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun LocalDate.toDisplayString(): String {
    return this.format(DateTimeFormatter.ofPattern(stringResource(id = R.string.datetime_format_dd_MM_YYYY)))
}

@Composable
fun LocalTime.toDisplayString(): String {
    return this.format(DateTimeFormatter.ofPattern(stringResource(id = R.string.datetime_format_hh_mm)))
}

@Preview(showBackground = true)
@Composable
fun DateInputPreview() {
    SymptomTrackerTheme {
        DateInputField(
            date = LocalDate.now(),
            onDateChanged = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimeInputPreview() {
    SymptomTrackerTheme {
        TimeInputField(
            time = LocalTime.now(),
            onTimeChanged = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DateTimeInputRowPreview() {
    SymptomTrackerTheme {
        DateTimeInputRow(
            dateTimeInput = DateTimeInput(),
            onDateChanged = {},
            onTimeChanged = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimePickerDialogPreview() {
    SymptomTrackerTheme {
        TimePickerModal(
            time = LocalTime.now(),
            onDismissRequest = {},
            onTimeSelected = { },
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DatePickerModalPreview() {
    SymptomTrackerTheme {
        DatePickerModal(
            date = LocalDate.now(),
            onDismissRequest = {},
            onDateSelected = { },
        )
    }
}
