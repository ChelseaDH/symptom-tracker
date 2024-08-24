package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.model.FoodItem
import com.example.symptomtracker.core.model.FoodLog
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

/**
 * Data layer implementation for [FoodItem] and [FoodLog].
 */
interface FoodLogRepository {
    /**
     * Inserts an item.
     */
    suspend fun insertItem(foodItem: FoodItem): Long

    /**
     * Inserts an item with a given name if one does not exist, returning the ID of the existing record if it does.
     */
    suspend fun insertOrGetItemByName(name: String): FoodItem

    /**
     * Inserts a food log and it's associated items.
     */
    suspend fun insertFoodLog(foodLog: FoodLog)

    /**
     * Get all food item records.
     */
    fun getAllItems(): Flow<List<FoodItem>>

    /**
     * Get all food logs with their associated items.
     */
    fun getAllFoodLogs(): Flow<List<FoodLog>>

    /**
     * Get a food log with it's associated items, if it exists.
     */
    fun getFoodLog(id: Long): Flow<FoodLog?>

    /**
     * Get all food logs with their associated items between two dates.
     */
    fun getAllFoodLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<FoodLog>>

    /**
     * Deletes a food log and the links to it's associated items.
     */
    suspend fun deleteFoodLog(foodLog: FoodLog)

    /**
     * Deletes a food item.
     */
    suspend fun deleteFoodItem(foodItem: FoodItem)

    /**
     * Updates a food log and it's associated items.
     */
    suspend fun updateFoodLog(foodLog: FoodLog)

    /**
     * Updates a food item.
     */
    suspend fun updateFoodItem(foodItem: FoodItem)

    /**
     * Merges two food items.
     */
    suspend fun mergeFoodItems(foodItem: FoodItem, foodItemToMerge: FoodItem)

    /**
     * Gets a count of the food logs that an item belongs to.
     */
    suspend fun getCountOfLogsItemBelongsTo(foodItem: FoodItem): Int
}
