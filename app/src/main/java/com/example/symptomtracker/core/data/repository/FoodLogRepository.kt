package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.database.model.FoodLog
import com.example.symptomtracker.core.database.model.FoodLogWithItems
import com.example.symptomtracker.core.database.model.Item
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

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
     * Retrieves all [FoodLog] records with their associated [Item] records between two given dates.
     */
    fun getAllFoodLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<FoodLogWithItems>>

    /**
     * Insert [Item] record.
     */
    suspend fun insertItem(item: Item)
}
