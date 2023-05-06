package com.example.symptomtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.symptomtracker.data.food.FoodLog
import com.example.symptomtracker.data.food.FoodLogDao
import com.example.symptomtracker.data.food.FoodLogItemCrossRef
import com.example.symptomtracker.data.food.Item
import com.example.symptomtracker.data.symptom.Symptom
import com.example.symptomtracker.data.symptom.SymptomDao
import com.example.symptomtracker.data.symptom.SymptomLog
import com.example.symptomtracker.data.symptom.SymptomLogCrossRef

/**
 * Database class with singleton Instance object
 */
@Database(entities = [Symptom::class, SymptomLog::class, SymptomLogCrossRef::class, FoodLog::class, Item::class, FoodLogItemCrossRef::class],
    version = 1,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun symptomDao(): SymptomDao
    abstract fun foodLogDao(): FoodLogDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context = context,
                    klass = AppDatabase::class.java,
                    name = "app_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}