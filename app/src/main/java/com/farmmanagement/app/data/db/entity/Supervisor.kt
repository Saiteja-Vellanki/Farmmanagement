package com.farmmanagement.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "supervisors", indices = [Index("farmId", unique = true)])
data class Supervisor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmId: String,
    val supervisorName: String = "",
    val phoneNumber: String = "",
    val monthlySalary: Double = 0.0,
    val noSupervisor: Boolean = false,
)
