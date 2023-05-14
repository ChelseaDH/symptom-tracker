package com.example.symptomtracker.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R
import com.example.symptomtracker.ui.theme.SymptomTrackerTheme
import java.util.*

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

fun DateTimeInput.toDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(dateInputFields.year,
        dateInputFields.month,
        dateInputFields.day,
        timeInputFields.hour,
        timeInputFields.minute)

    return calendar.time
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInput(
    dateInputFields: DateInputFields,
    onDateChanged: (DateInputFields) -> Unit,
    labelOnTextField: Boolean,
    modifier: Modifier = Modifier,
    labelSpacing: Dp = 4.dp,
) {
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, dpYear: Int, dpMonth: Int, dpDayOfMonth: Int ->
            onDateChanged(DateInputFields(dpYear, dpMonth, dpDayOfMonth))
        }, dateInputFields.year, dateInputFields.month, dateInputFields.day
    )

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
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_date_cd))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInput(
    timeInput: TimeInputFields,
    onTimeChanged: (TimeInputFields) -> Unit,
    labelOnTextField: Boolean,
    modifier: Modifier = Modifier,
    labelSpacing: Dp = 4.dp,
) {
    val timePickerDialog = TimePickerDialog(
        LocalContext.current,
        { _: TimePicker, hour: Int, minute: Int ->
            onTimeChanged(TimeInputFields(hour, minute))
        }, timeInput.hour, timeInput.minute, true
    )

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
                IconButton(onClick = { timePickerDialog.show() }) {
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
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            TimeInput(
                timeInput = dateTimeInput.timeInputFields,
                onTimeChanged = onTimeChanged,
                labelOnTextField = labelOnTextField,
                labelSpacing = labelSpacing,
            )
        }
    }
}

@Preview
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
            DateInput(dateInputFields = dateInputFields,
                onDateChanged = {},
                labelOnTextField = true)
            DateInput(dateInputFields = dateInputFields,
                onDateChanged = {},
                labelOnTextField = false)
            TimeInput(timeInput = timeInputFields, onTimeChanged = {}, labelOnTextField = true)
            TimeInput(timeInput = timeInputFields, onTimeChanged = {}, labelOnTextField = false)
            DateTimeInputRow(
                dateTimeInput = DateTimeInput(dateInputFields, timeInputFields),
                onDateChanged = {},
                onTimeChanged = {},
                labelOnTextField = false
            )
        }
    }
}