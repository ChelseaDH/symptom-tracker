package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.FoodItemEntity
import com.example.symptomtracker.core.model.FoodItem
import org.junit.Assert.assertEquals
import org.junit.Test

class FoodItemKtTest {

    @Test
    fun foodItem_canBeMappedToEntity() {
        val foodItemModel = FoodItem(id = 1, name = "banana")
        val expectedEntity = FoodItemEntity(id = 1, name = "banana")

        assertEquals(expectedEntity, foodItemModel.asEntity())
    }
}
