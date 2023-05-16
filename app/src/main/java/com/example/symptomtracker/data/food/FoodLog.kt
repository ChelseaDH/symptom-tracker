package com.example.symptomtracker.data.food

import androidx.room.*
import java.util.*

@Entity(tableName = "food_log")
data class FoodLog(
    @PrimaryKey(autoGenerate = true) val foodLogId: Long,
    val date: Date,
)

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true) val itemId: Long,
    val name: String,
)

@Entity(
    tableName = "food_log_item",
    primaryKeys = ["foodLogId", "itemId"],
    indices = [Index("itemId")]
)
data class FoodLogItemCrossRef(
    val foodLogId: Long,
    val itemId: Long,
)

data class FoodLogWithItems(
    @Embedded val foodLog: FoodLog,
    @Relation(
        parentColumn = "foodLogId",
        entityColumn = "itemId",
        associateBy = Junction(FoodLogItemCrossRef::class)
    )
    val items: List<Item>,
)