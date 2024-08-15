package com.example.symptomtracker.core.testing.repository

import com.example.symptomtracker.core.data.repository.FoodLogRepository
import com.example.symptomtracker.core.model.FoodItem
import com.example.symptomtracker.core.model.FoodLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime

class TestFoodRepository : FoodLogRepository {
    private val itemsFlow: MutableSharedFlow<List<FoodItem>> = MutableSharedFlow(replay = 1)
    private val foodLogsFlow: MutableSharedFlow<List<FoodLog>> = MutableSharedFlow(replay = 1)

    override suspend fun insertItem(foodItem: FoodItem): Long = 1

    override suspend fun insertFoodLog(foodLog: FoodLog) {}

    override fun getAllItems(): Flow<List<FoodItem>> = itemsFlow

    override fun getAllFoodLogs(): Flow<List<FoodLog>> = foodLogsFlow

    override fun getFoodLog(id: Long): Flow<FoodLog?> =
        foodLogsFlow.map { foodLogs -> foodLogs.find { it.id == id } }

    override fun getAllFoodLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<FoodLog>> =
        foodLogsFlow.map { foodLogs ->
            foodLogs.filter {
                it.date.isAfter(startDate) && it.date.isBefore(
                    endDate
                )
            }
        }

    override suspend fun deleteFoodLog(foodLog: FoodLog) {}

    override suspend fun deleteFoodItem(foodItem: FoodItem) {
        itemsFlow.tryEmit(itemsFlow.replayCache.first().filter { it.id != foodItem.id })
    }

    override suspend fun updateFoodLog(foodLog: FoodLog) {}

    override suspend fun updateFoodItem(foodItem: FoodItem) {
        itemsFlow.tryEmit(itemsFlow.replayCache.first().map {
            if (it.id == foodItem.id) foodItem else it
        })
    }

    override suspend fun mergeFoodItems(foodItem: FoodItem, foodItemToMerge: FoodItem) {
        itemsFlow.tryEmit(itemsFlow.replayCache.first().filter { it != foodItemToMerge })
    }

    override suspend fun getCountOfLogsItemBelongsTo(foodItem: FoodItem): Int =
        foodLogsFlow.replayCache.first().count { it.items.contains(foodItem) }

    /**
     * A test-only API to allow controlling the list of food items from tests.
     */
    fun sendFoodItems(topics: List<FoodItem>) {
        itemsFlow.tryEmit(topics)
    }

    /**
     * A test-only API to allow controlling the list of food logs from tests.
     */
    fun sendFoodLogs(topics: List<FoodLog>) {
        foodLogsFlow.tryEmit(topics)
    }
}
