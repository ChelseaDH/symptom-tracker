package com.example.symptomtracker.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.core.model.Severity
import com.example.symptomtracker.core.model.Symptom
import com.example.symptomtracker.core.model.SymptomLog
import com.example.symptomtracker.core.model.SymptomWithSeverity
import java.time.OffsetDateTime

class SymptomLogPreviewParameterProvider :
    PreviewParameterProvider<SymptomLog> {
    override val values: Sequence<SymptomLog> = sequenceOf(
        SymptomLog(
            id = 1,
            date = OffsetDateTime.parse("2024-05-01T08:30:00+00:00"),
            items = listOf(
                SymptomWithSeverity(
                    symptom = Symptom(id = 1, name = "Bloating"),
                    severity = Severity.MILD
                )
            )
        ),
        SymptomLog(
            id = 2,
            date = OffsetDateTime.parse("2024-04-29T10:30:00+00:00"),
            items = listOf(
                SymptomWithSeverity(
                    symptom = Symptom(id = 2, name = "Fatigue"),
                    severity = Severity.MODERATE
                )
            )
        )
    )
}
