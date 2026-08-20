package com.farmmanagement.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "motors", indices = [Index("farmId")])
data class Motor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmId: String,
    val motorType: String,
    val motorHp: String,
)
