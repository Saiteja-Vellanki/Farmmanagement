package com.farmmanagement.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.farmmanagement.app.data.db.entity.Farm
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {
    @Query("SELECT * FROM farms ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Farm>>

    @Query("SELECT * FROM farms WHERE farmId = :farmId LIMIT 1")
    fun observeByFarmId(farmId: String): Flow<Farm?>

    @Insert
    suspend fun insert(farm: Farm)
}
