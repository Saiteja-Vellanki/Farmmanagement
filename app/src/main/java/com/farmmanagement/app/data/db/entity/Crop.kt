package com.farmmanagement.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "crops", indices = [Index("farmId")])
data class Crop(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmId: String,
    val plantName: String,
    val numberOfPlants: Int,
)
