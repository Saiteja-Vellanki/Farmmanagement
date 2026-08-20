package com.farmmanagement.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.farmmanagement.app.data.db.entity.Supervisor
import kotlinx.coroutines.flow.Flow

@Dao
interface SupervisorDao {
    @Query("SELECT * FROM supervisors WHERE farmId = :farmId LIMIT 1")
    fun observeForFarm(farmId: String): Flow<Supervisor?>

    @Insert
    suspend fun insert(supervisor: Supervisor)
}
