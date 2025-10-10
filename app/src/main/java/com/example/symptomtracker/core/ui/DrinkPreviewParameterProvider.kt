package com.example.symptomtracker.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.core.domain.model.DrinkItem

class DrinkItemPreviewParameterProvider : PreviewParameterProvider<List<DrinkItem>> {
    override val values: Sequence<List<DrinkItem>> = sequenceOf(
        listOf(
            DrinkItem(1, "Water"),
            DrinkItem(2, "Coffee"),
            DrinkItem(3, "Tea"),
            DrinkItem(5, "Orange juice"),
            DrinkItem(6, "Milk"),
        )
    )
}