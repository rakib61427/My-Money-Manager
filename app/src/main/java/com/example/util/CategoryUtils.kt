package com.example.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class FinancialCategory(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val icon: ImageVector,
    val color: Color
)

object CategoryUtils {
    val incomeCategories = listOf(
        FinancialCategory("Salary", "Salary", "বেতন", Icons.Default.LocalAtm, Color(0xFF10B981)),
        FinancialCategory("Business", "Business", "ব্যবসা", Icons.Default.Work, Color(0xFF3B82F6)),
        FinancialCategory("Freelance", "Freelance", "ফ্রিল্যান্স", Icons.Default.LaptopMac, Color(0xFF8B5CF6)),
        FinancialCategory("Investment", "Investment", "বিনিয়োগ", Icons.Default.TrendingUp, Color(0xFF059669)),
        FinancialCategory("Bonus", "Bonus", "বোনাস", Icons.Default.AccountBalanceWallet, Color(0xFFF59E0B)),
        FinancialCategory("Gift", "Gift", "উপহার", Icons.Default.CardGiftcard, Color(0xFFEC4899)),
        FinancialCategory("Rental", "Rental", "ভাড়া", Icons.Default.Home, Color(0xFF0EA5E9)),
        FinancialCategory("Other", "Other", "অন্যান্য", Icons.Default.Payment, Color(0xFF64748B))
    )

    val expenseCategories = listOf(
        FinancialCategory("Food", "Food", "খাবার", Icons.Default.Restaurant, Color(0xFFEF4444)),
        FinancialCategory("Grocery", "Grocery", "মুদি বাজার", Icons.Default.ShoppingCart, Color(0xFFF97316)),
        FinancialCategory("Shopping", "Shopping", "কেনাকাটা", Icons.Default.ShoppingBag, Color(0xFFEC4899)),
        FinancialCategory("Transport", "Transport", "যাতায়াত", Icons.Default.DirectionsBus, Color(0xFF3B82F6)),
        FinancialCategory("Education", "Education", "শিক্ষা", Icons.Default.School, Color(0xFF8B5CF6)),
        FinancialCategory("Medical", "Medical", "চিকিৎসা", Icons.Default.LocalHospital, Color(0xFF14B8A6)),
        FinancialCategory("Bills", "Bills", "বিল ও ইউটিলিটি", Icons.Default.Receipt, Color(0xFFEAB308)),
        FinancialCategory("Rent", "Rent", "ভাড়া", Icons.Default.Home, Color(0xFF6366F1)),
        FinancialCategory("Entertainment", "Entertainment", "বিনোদন", Icons.Default.Movie, Color(0xFFA855F7)),
        FinancialCategory("Investment", "Investment", "বিনিয়োগ", Icons.Default.TrendingUp, Color(0xFF10B981)),
        FinancialCategory("Gift", "Gift", "উপহার", Icons.Default.CardGiftcard, Color(0xFFF43F5E)),
        FinancialCategory("Other", "Other", "অন্যান্য", Icons.Default.Payment, Color(0xFF64748B))
    )

    val paymentMethods = listOf("Cash", "bKash", "Nagad", "Rocket", "Bank", "Card", "Other")

    fun getCategoryInfo(name: String, isIncome: Boolean = false): FinancialCategory {
        val list = if (isIncome) incomeCategories else expenseCategories
        val found = list.find { it.id.equals(name, ignoreCase = true) || it.nameBn.equals(name, ignoreCase = true) }
        if (found != null) return found

        // Check alternate list
        val altList = if (isIncome) expenseCategories else incomeCategories
        val altFound = altList.find { it.id.equals(name, ignoreCase = true) || it.nameBn.equals(name, ignoreCase = true) }
        if (altFound != null) return altFound

        // Fallback for custom categories
        return FinancialCategory(
            id = name,
            nameEn = name,
            nameBn = name,
            icon = Icons.Default.Payment,
            color = Color(0xFF0D9488)
        )
    }

    fun getDisplayName(categoryName: String, lang: String): String {
        val item = getCategoryInfo(categoryName)
        return if (lang == Localization.LANG_BN) item.nameBn else item.nameEn
    }
}
