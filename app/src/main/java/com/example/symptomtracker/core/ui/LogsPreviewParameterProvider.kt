package com.example.symptomtracker.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.core.database.model.MovementLog
import com.example.symptomtracker.core.model.FoodItem
import com.example.symptomtracker.core.model.FoodLog
import com.example.symptomtracker.core.model.Log
import com.example.symptomtracker.core.model.Severity
import com.example.symptomtracker.core.model.StoolType
import com.example.symptomtracker.core.model.Symptom
import com.example.symptomtracker.core.model.SymptomLog
import com.example.symptomtracker.core.model.SymptomWithSeverity
import java.time.OffsetDateTime

class LogsPreviewParameterProvider : PreviewParameterProvider<List<Log>> {
    override val values: Sequence<List<Log>> = sequenceOf(
        listOf(
            FoodLog(
                id = 1,
                date = OffsetDateTime.parse("2023-03-02T08:30:00+00:00"),
                items = listOf(
                    FoodItem(1, "Banana"),
                    FoodItem(2, "Strawberries"),
                    FoodItem(3, "Blueberries"),
                    FoodItem(4, "Yogurt"),
                )
            ),
            MovementLog(
                movementLogId = 1,
                date = OffsetDateTime.parse("2023-03-02T09:10:00+00:00"),
                stoolType = StoolType.NORMAL_3,
            ),
            FoodLog(
                id = 2,
                date = OffsetDateTime.parse("2023-03-02T12:15:00+00:00"),
                items = listOf(
                    FoodItem(5, "Chicken"),
                    FoodItem(6, "Rice"),
                    FoodItem(7, "Beans"),
                )
            ),
            SymptomLog(
                id = 1,
                date = OffsetDateTime.parse("2023-03-02T13:15:00+00:00"),
                items = listOf(
                    SymptomWithSeverity(
                        symptom = Symptom(1, "Bloating"),
                        severity = Severity.MILD
                    )
                )
            )
        )
    )
}
