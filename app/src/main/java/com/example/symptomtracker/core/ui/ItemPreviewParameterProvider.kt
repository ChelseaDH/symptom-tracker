package com.example.symptomtracker.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.core.model.FoodItem

class ItemPreviewParameterProvider : PreviewParameterProvider<List<FoodItem>> {
    override val values: Sequence<List<FoodItem>> = sequenceOf(
        listOf(
            FoodItem(1, "Banana"),
            FoodItem(2, "Strawberries"),
            FoodItem(3, "Blueberries"),
            FoodItem(4, "Yogurt"),
            FoodItem(5, "Chicken"),
            FoodItem(6, "Rice"),
            FoodItem(7, "Beans"),
        )
    )
}
