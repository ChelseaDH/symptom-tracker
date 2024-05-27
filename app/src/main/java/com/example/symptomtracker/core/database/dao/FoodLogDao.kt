package com.example.symptomtracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.symptomtracker.core.database.model.FoodLog
import com.example.symptomtracker.core.database.model.FoodLogItemCrossRef
import com.example.symptomtracker.core.database.model.FoodLogWithItems
import com.example.symptomtracker.core.database.model.Item
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
    @Query("SELECT * FROM food_log ORDER BY date DESC")
    fun getAllFoodLogs(): Flow<List<FoodLogWithItems>>

    @Transaction
    @Query("SELECT * FROM food_log WHERE date BETWEEN :startDate AND :endDate")
    fun getAllFoodLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<FoodLogWithItems>>

    @Transaction
    @Query("SELECT * FROM food_log WHERE foodLogId = :id")
    fun getFoodLog(id: Long): Flow<FoodLogWithItems>

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

    @Delete
    suspend fun deleteLog(foodLog: FoodLog)
}
