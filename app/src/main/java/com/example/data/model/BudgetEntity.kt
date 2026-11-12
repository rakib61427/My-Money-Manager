package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val category: String,
    val monthYear: String, // e.g. "2026-11"
    val amount: Double,
    val createdAt: Long = System.currentTimeMillis()
)
