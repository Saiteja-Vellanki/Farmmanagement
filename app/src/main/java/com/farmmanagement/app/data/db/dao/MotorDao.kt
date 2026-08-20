package com.farmmanagement.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.farmmanagement.app.data.db.entity.Motor
import kotlinx.coroutines.flow.Flow

@Dao
interface MotorDao {
    @Query("SELECT * FROM motors WHERE farmId = :farmId")
    fun observeForFarm(farmId: String): Flow<List<Motor>>

    @Query("SELECT COUNT(*) FROM motors WHERE farmId = :farmId")
    fun observeCountForFarm(farmId: String): Flow<Int>

    @Insert
    suspend fun insertAll(motors: List<Motor>)
}
