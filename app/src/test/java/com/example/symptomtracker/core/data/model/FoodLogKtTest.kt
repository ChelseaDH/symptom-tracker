package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.FoodItemEntity
import com.example.symptomtracker.core.database.model.FoodLogEntity
import com.example.symptomtracker.core.database.model.PopulatedFoodLog
import com.example.symptomtracker.core.domain.model.FoodItem
import com.example.symptomtracker.core.domain.model.FoodLog
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.OffsetDateTime

class FoodLogKtTest {

    @Test
    fun foodLog_canBeMappedToEntity() {
        val foodLogModel = FoodLog(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T12:15:00+00:00"),
            items = listOf(FoodItem(id = 2, name = "banana"))
        )
        val expectedEntity = PopulatedFoodLog(
            log = FoodLogEntity(id = 1, date = OffsetDateTime.parse("2023-03-02T12:15:00+00:00")),
            foodItemEntities = listOf(FoodItemEntity(id = 2, name = "banana"))
        )

        assertEquals(expectedEntity, foodLogModel.asEntity())
    }

    @Test
    fun foodLog_canBeMappedToFoodLogEntity() {
        val foodLogModel = FoodLog(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T12:15:00+00:00"),
            items = listOf(FoodItem(id = 2, name = "banana"))
        )
        val expectedEntity =
            FoodLogEntity(id = 1, date = OffsetDateTime.parse("2023-03-02T12:15:00+00:00"))

        assertEquals(expectedEntity, foodLogModel.asFoodLogEntity())
    }
}
