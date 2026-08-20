package com.farmmanagement.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.farmmanagement.app.data.db.entity.Crop
import kotlinx.coroutines.flow.Flow

@Dao
interface CropDao {
    @Query("SELECT * FROM crops WHERE farmId = :farmId")
    fun observeForFarm(farmId: String): Flow<List<Crop>>

    @Query("SELECT COUNT(*) FROM crops WHERE farmId = :farmId")
    fun observeCountForFarm(farmId: String): Flow<Int>

    @Insert
    suspend fun insertAll(crops: List<Crop>)
}
