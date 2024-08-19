package com.example.symptomtracker.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.symptomtracker.core.database.dao.FoodLogDao
import com.example.symptomtracker.core.database.dao.MovementDao
import com.example.symptomtracker.core.database.dao.SymptomDao
import com.example.symptomtracker.core.database.model.FoodItemEntity
import com.example.symptomtracker.core.database.model.FoodLogEntity
import com.example.symptomtracker.core.database.model.FoodLogItemCrossRef
import com.example.symptomtracker.core.database.model.MovementLogEntity
import com.example.symptomtracker.core.database.model.SymptomEntity
import com.example.symptomtracker.core.database.model.SymptomLogEntity
import com.example.symptomtracker.core.database.model.SymptomLogSymptomCrossRef
import com.example.symptomtracker.core.database.util.Converters

/**
 * Database class with singleton Instance object
 */
@Database(
    entities = [SymptomEntity::class, SymptomLogEntity::class, SymptomLogSymptomCrossRef::class, FoodLogEntity::class, FoodItemEntity::class, FoodLogItemCrossRef::class, MovementLogEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun symptomDao(): SymptomDao
    abstract fun foodLogDao(): FoodLogDao
    abstract fun movementDao(): MovementDao

    companion object {
        const val DATABASE_NAME = "app_database"

        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = AppDatabase::class.java,
                    name = DATABASE_NAME,
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
