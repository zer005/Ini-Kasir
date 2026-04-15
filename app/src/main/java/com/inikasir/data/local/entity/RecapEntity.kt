package com.inikasir.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recaps")
data class RecapEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDate: Long,
    val endDate: Long,
    val totalRevenue: Double,
    val transactionCount: Int,
    val createdAt: Long = System.currentTimeMillis()
)