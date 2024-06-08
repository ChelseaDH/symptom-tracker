package com.example.symptomtracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.symptomtracker.core.database.model.FoodItemEntity
import com.example.symptomtracker.core.database.model.FoodLogEntity
import com.example.symptomtracker.core.database.model.FoodLogItemCrossRef
import com.example.symptomtracker.core.database.model.PopulatedFoodLog
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface FoodLogDao {
    @Insert
    fun insertFoodLog(foodLogEntity: FoodLogEntity): Long

    @Insert
    suspend fun insertItem(foodItemEntity: FoodItemEntity): Long

    @Query("SELECT * FROM item ORDER BY name ASC")
    fun getAllItems(): Flow<List<FoodItemEntity>>

    @Transaction
    @Query("SELECT * FROM food_log ORDER BY date DESC")
    fun getAllFoodLogs(): Flow<List<PopulatedFoodLog>>

    @Transaction
    @Query("SELECT * FROM food_log WHERE date BETWEEN :startDate AND :endDate")
    fun getAllFoodLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<PopulatedFoodLog>>

    @Transaction
    @Query("SELECT * FROM food_log WHERE id = :id")
    fun getFoodLog(id: Long): Flow<PopulatedFoodLog?>

    @Insert
    suspend fun insertFoodLogItemCrossRef(foodLogItemCrossRef: FoodLogItemCrossRef)

    @Transaction
    suspend fun insertFoodLogAndAssociatedRecords(populatedFoodLog: PopulatedFoodLog) {
        val foodLogId = insertFoodLog(populatedFoodLog.log)

        populatedFoodLog.foodItemEntities.forEach { item ->
            insertFoodLogItemCrossRef(
                foodLogItemCrossRef = FoodLogItemCrossRef(
                    foodLogId = foodLogId,
                    itemId = item.id
                )
            )
        }
    }

    @Delete
    suspend fun deleteLog(foodLogEntity: FoodLogEntity)

    @Query("DELETE FROM food_log_item WHERE foodLogId = :id")
    suspend fun deleteAllCrossRefItemsForLogById(id: Long)

    @Update
    suspend fun updateLog(foodLogEntity: FoodLogEntity)

    @Transaction
    suspend fun updateLogAndAssociatedRecords(populatedFoodLog: PopulatedFoodLog) {
        deleteAllCrossRefItemsForLogById(populatedFoodLog.log.id)
        updateLog(populatedFoodLog.log)

        populatedFoodLog.foodItemEntities.forEach { item ->
            insertFoodLogItemCrossRef(
                foodLogItemCrossRef = FoodLogItemCrossRef(
                    foodLogId = populatedFoodLog.log.id,
                    itemId = item.id,
                )
            )
        }
    }
}
