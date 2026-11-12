package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: String, // "INCOME", "EXPENSE", "GIVEN", "RECEIVED"
    val amount: Double,
    val category: String, // "Salary", "Food", "Rent", "Person Payment", etc.
    val source: String = "", // "Cash", "bKash", "Bank", etc.
    val personId: Long? = null,
    val personName: String? = null,
    val note: String = "",
    val datetime: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

object TransactionType {
    const val INCOME = "INCOME"
    const val EXPENSE = "EXPENSE"
    const val GIVEN = "GIVEN"
    const val RECEIVED = "RECEIVED"
}
