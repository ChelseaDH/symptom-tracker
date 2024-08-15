package com.example.symptomtracker.feature.food

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.core.model.FoodItem

class EditActionStateProvider : PreviewParameterProvider<ActionState.Edit> {
    override val values: Sequence<ActionState.Edit>
        get() = sequenceOf(
            ActionState.Edit(
                foodItem = FoodItem(
                    id = 1,
                    name = "Apple"
                ),
                name = ""
            ),
            ActionState.Edit(
                foodItem = FoodItem(
                    id = 1,
                    name = "Apple"
                ),
                name = "Apples"
            ),
        )
}

class MergeDeleteActionStateProvider : PreviewParameterProvider<ActionState.Delete.Merge> {
    override val values: Sequence<ActionState.Delete.Merge>
        get() = sequenceOf(
            ActionState.Delete.Merge(
                foodItem = FoodItem(
                    id = 1,
                    name = "Apple"
                ),
                mergeCandidates = listOf(
                    FoodItem(
                        id = 2,
                        name = "Banana"
                    ),
                    FoodItem(
                        id = 3,
                        name = "Carrot"
                    ),
                ),
            ),
            ActionState.Delete.Merge(
                foodItem = FoodItem(
                    id = 1,
                    name = "Apple"
                ),
                mergeCandidates = listOf(
                    FoodItem(
                        id = 2,
                        name = "Banana"
                    ),
                ),
                chosenItem = FoodItem(
                    id = 2,
                    name = "Banana"
                ),
            ),
        )
}
