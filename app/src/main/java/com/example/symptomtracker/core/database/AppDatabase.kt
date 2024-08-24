package com.example.symptomtracker.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun symptomDao(): SymptomDao
    abstract fun foodLogDao(): FoodLogDao
    abstract fun movementDao(): MovementDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = AppDatabase::class.java,
                    name = "app_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { Instance = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE UNIQUE INDEX index_item_name ON item (name)")
            }
        }
    }
}
