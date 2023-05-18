package com.example.symptomtracker.data.food

import androidx.room.*
import java.util.*

@Entity(tableName = "food_log")
data class FoodLog(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val date: Date,
)

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
)

@Entity(
    tableName = "food_log_item",
    primaryKeys = ["food_log_id", "item_id"],
    foreignKeys = [
        ForeignKey(
            entity = FoodLog::class,
            parentColumns = ["id"],
            childColumns = ["food_log_id"]
        ),
        ForeignKey(entity = Item::class, parentColumns = ["id"], childColumns = ["item_id"])
    ],
    indices = [Index("item_id")]
)
data class FoodLogItemCrossRef(
    @ColumnInfo(name = "food_log_id") val foodLogId: Long,
    @ColumnInfo(name = "item_id") val itemId: Long,
)

data class FoodLogWithItems(
    val foodLog: FoodLog,
    val items: List<Item>,
)