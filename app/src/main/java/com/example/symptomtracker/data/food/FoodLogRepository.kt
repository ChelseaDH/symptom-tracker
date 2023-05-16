package com.example.symptomtracker.data.food

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert and retrieval of [FoodLogWithItems] and [Item] records from a given data source.
 */
interface FoodLogRepository {
    /**
     * Insert [FoodLog] and associated [Item] records via the [FoodLogWithItems] object.
     */
    suspend fun insertFoodLogWithItems(foodLogWithItems: FoodLogWithItems)

    /**
     * Retrieves all [Item] records.
     */
    fun getAllItemsStream(): Flow<List<Item>>

    /**
     * Retrieves all [FoodLog] records with their associated [Item] records.
     */
    fun getAllFoodLogs(): Flow<List<FoodLogWithItems>>

    /**
     * Insert [Item] record.
     */
    suspend fun insertItem(item: Item)
}