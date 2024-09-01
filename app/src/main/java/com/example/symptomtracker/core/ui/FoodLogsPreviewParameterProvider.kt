package com.example.symptomtracker.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.core.domain.model.FoodItem
import com.example.symptomtracker.core.domain.model.FoodLog
import java.time.OffsetDateTime

class FoodLogsPreviewParameterProvider : PreviewParameterProvider<List<FoodLog>> {
    override val values: Sequence<List<FoodLog>> = sequenceOf(
        listOf(
            FoodLog(
                id = 1, date = OffsetDateTime.parse("2023-03-02T08:30:00+00:00"), items = listOf(
                    FoodItem(1, "Banana"),
                    FoodItem(2, "Strawberries"),
                    FoodItem(3, "Blueberries"),
                    FoodItem(4, "Yogurt"),
                )
            ), FoodLog(
                id = 2, date = OffsetDateTime.parse("2023-03-02T12:15:00+00:00"), items = listOf(
                    FoodItem(5, "Chicken"),
                    FoodItem(6, "Rice"),
                    FoodItem(7, "Beans"),
                )
            )
        )
    )
}

class FoodLogPreviewParameterProvider : PreviewParameterProvider<FoodLog> {
    override val values = sequenceOf(
        FoodLog(
            id = 1, date = OffsetDateTime.now(), items = listOf(
                FoodItem(1, "Banana"),
                FoodItem(2, "Oats"),
            )
        ),
    )
}
