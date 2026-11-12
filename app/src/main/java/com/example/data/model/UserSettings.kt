package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val currencyCode: String = "BDT",
    val currencySymbol: String = "৳",
    val language: String = "bn", // "bn" or "en"
    val darkMode: Int = 0, // 0 = System, 1 = Light, 2 = Dark
    val openingBalance: Double = 0.0,
    val isBiometricEnabled: Boolean = false
)
