package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "people")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val phone: String = "",
    val type: String, // "OWE" (I have to pay), "RECEIVABLE" (I will receive)
    val totalAmount: Double,
    val paidAmount: Double = 0.0,
    val purpose: String = "",
    val dueDate: Long? = null,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val remainingAmount: Double
        get() = (totalAmount - paidAmount).coerceAtLeast(0.0)

    val progressPercent: Float
        get() = if (totalAmount > 0) ((paidAmount / totalAmount) * 100f).toFloat().coerceIn(0f, 100f) else 0f
}

object PersonType {
    const val OWE = "OWE" // I Have To Pay
    const val RECEIVABLE = "RECEIVABLE" // I Will Receive
}
