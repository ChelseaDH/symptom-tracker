package com.example.symptomtracker.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.core.database.model.Symptom
import com.example.symptomtracker.core.database.model.SymptomLog
import com.example.symptomtracker.core.model.Severity
import com.example.symptomtracker.core.model.SymptomLogWithSymptoms
import com.example.symptomtracker.core.model.SymptomWithSeverity
import java.time.OffsetDateTime

class SymptomLogPreviewParameterProvider :
    PreviewParameterProvider<SymptomLogWithSymptoms> {
    override val values: Sequence<SymptomLogWithSymptoms> = sequenceOf(
        SymptomLogWithSymptoms(
            log = SymptomLog(
                symptomLogId = 1,
                OffsetDateTime.parse("2024-05-01T08:30:00+00:00")
            ),
            items = listOf(
                SymptomWithSeverity(
                    symptom = Symptom(symptomId = 1, name = "Bloating"),
                    severity = Severity.MILD
                )
            )
        ),
        SymptomLogWithSymptoms(
            log = SymptomLog(
                symptomLogId = 2,
                OffsetDateTime.parse("2024-04-29T10:30:00+00:00")
            ),
            items = listOf(
                SymptomWithSeverity(
                    symptom = Symptom(symptomId = 2, name = "Fatigue"),
                    severity = Severity.MODERATE
                )
            )
        )
    )
}
