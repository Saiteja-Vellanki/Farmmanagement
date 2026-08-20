package com.farmmanagement.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.farmmanagement.app.data.db.dao.CropDao
import com.farmmanagement.app.data.db.dao.FarmDao
import com.farmmanagement.app.data.db.dao.MotorDao
import com.farmmanagement.app.data.db.dao.SupervisorDao
import com.farmmanagement.app.data.db.entity.Crop
import com.farmmanagement.app.data.db.entity.Farm
import com.farmmanagement.app.data.db.entity.Motor
import com.farmmanagement.app.data.db.entity.Supervisor

/**
 * Phase 1 schema: Farm, Crop, Motor, Supervisor — everything else (Workers,
 * Fertilizer, Spraying, Irrigation, Machinery, Purchase, Harvest, Store,
 * Expenses) is deliberately out of scope until later phases, per the Phase 1
 * spec. No app has shipped against this schema yet, so version stays at 1;
 * once Phase 1 is released, any further schema change must ship an explicit
 * Room Migration rather than a version bump with no migration path.
 */
@Database(
    entities = [Farm::class, Crop::class, Motor::class, Supervisor::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun farmDao(): FarmDao
    abstract fun cropDao(): CropDao
    abstract fun motorDao(): MotorDao
    abstract fun supervisorDao(): SupervisorDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "farmmanagement.db",
                ).build().also { INSTANCE = it }
            }
    }
}
