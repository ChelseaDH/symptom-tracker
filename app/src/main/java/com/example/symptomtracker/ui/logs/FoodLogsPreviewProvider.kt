package com.example.symptomtracker.ui.logs

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.data.food.FoodLog
import com.example.symptomtracker.data.food.FoodLogWithItems
import com.example.symptomtracker.data.food.Item
import java.time.OffsetDateTime

class FoodLogsPreviewProvider : PreviewParameterProvider<List<FoodLogWithItems>> {
    override val values: Sequence<List<FoodLogWithItems>> = sequenceOf(
        listOf(
            FoodLogWithItems(
                log = FoodLog(1, OffsetDateTime.now()),
                items = listOf(
                    Item(1, "Banana"),
                    Item(2, "Strawberries"),
                    Item(3, "Blueberries"),
                    Item(4, "Yogurt"),
                )
            ),
            FoodLogWithItems(
                log = FoodLog(2, OffsetDateTime.now()),
                items = listOf(
                    Item(5, "Chicken"),
                    Item(6, "Rice"),
                    Item(7, "Beans"),
                )
            )
        )
    )
}
