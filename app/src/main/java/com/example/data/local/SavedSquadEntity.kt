package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_squads")
data class SavedSquadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val raceId: String,
    val raceName: String,
    val budgetLimit: Int,
    val totalSpent: Int,
    val expectedPoints: Double,
    val startersIds: String, // Comma separated rider IDs
    val reserveId: Int?,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)
