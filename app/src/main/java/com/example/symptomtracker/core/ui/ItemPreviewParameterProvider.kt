package com.example.symptomtracker.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.core.database.model.Item

class ItemPreviewParameterProvider : PreviewParameterProvider<List<Item>> {
    override val values: Sequence<List<Item>> = sequenceOf(
        listOf(
            Item(1, "Banana"),
            Item(2, "Strawberries"),
            Item(3, "Blueberries"),
            Item(4, "Yogurt"),
            Item(5, "Chicken"),
            Item(6, "Rice"),
            Item(7, "Beans"),
        )
    )
}
