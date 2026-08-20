package com.farmmanagement.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Farm record. Per spec section 23, `farmId` — not the Room row `id` and not
 * the farm name — is the relationship key every child table (Crop, Motor,
 * Supervisor, and every future module) is scoped by. `id` remains as Room's
 * own autoincrement row identity for internal DB bookkeeping only; it is
 * never used for navigation or cross-table relationships.
 */
@Entity(tableName = "farms", indices = [Index("farmId", unique = true)])
data class Farm(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmId: String,
    val farmName: String,
    val farmAddress: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val totalExtent: Double = 0.0,
    val extentUnit: String = "Acres",
    val farmPhotoPath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
