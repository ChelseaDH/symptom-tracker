package com.example.symptomtracker.data.food

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface FoodLogDao {
    @Insert
    fun insertFoodLog(foodLog: FoodLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItems(items: List<Item>): Array<Long>

    @Insert
    suspend fun insertItem(item: Item)

    @Query("SELECT * FROM item ORDER BY name ASC")
    fun getAllItems(): Flow<List<Item>>

    @Transaction
    @Query("SELECT * FROM food_log")
    fun getAllFoodLogs(): Flow<List<FoodLogWithItems>>

    @Transaction
    @Query("SELECT * FROM food_log WHERE date BETWEEN :startDate AND :endDate")
    fun getAllFoodLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<FoodLogWithItems>>

    @Insert
    suspend fun insertFoodLogItemCrossRef(foodLogItemCrossRef: FoodLogItemCrossRef)

    @Transaction
    suspend fun insertFoodLogWithItems(foodLogWithItems: FoodLogWithItems) {
        val foodLogId = insertFoodLog(foodLogWithItems.log)
        val itemLogIds = insertItems(foodLogWithItems.items)

        itemLogIds.forEach { itemId ->
            insertFoodLogItemCrossRef(
                foodLogItemCrossRef = FoodLogItemCrossRef(
                    foodLogId = foodLogId,
                    itemId = itemId
                )
            )
        }
    }
}
