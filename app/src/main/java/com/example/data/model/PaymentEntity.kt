package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val personId: Long,
    val amount: Double,
    val type: String, // "GIVEN", "RECEIVED"
    val datetime: Long = System.currentTimeMillis(),
    val method: String = "Cash", // Cash, bKash, Nagad, Bank, Card, Other
    val note: String = "",
    val transactionId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
