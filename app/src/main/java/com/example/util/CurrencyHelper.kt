package com.example.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

data class CurrencyItem(
    val code: String,
    val nameEn: String,
    val nameBn: String,
    val symbol: String,
    val flag: String
)

object CurrencyHelper {
    val supportedCurrencies = listOf(
        CurrencyItem("BDT", "Bangladeshi Taka", "বাংলাদেশী টাকা", "৳", "🇧🇩"),
        CurrencyItem("USD", "US Dollar", "ইউএস ডলার", "$", "🇺🇸"),
        CurrencyItem("EUR", "Euro", "ইউরো", "€", "🇪🇺"),
        CurrencyItem("GBP", "UK Pound", "ব্রিটিশ পাউন্ড", "£", "🇬🇧"),
        CurrencyItem("INR", "Indian Rupee", "ভারতীয় রুপি", "₹", "🇮🇳"),
        CurrencyItem("AUD", "Australian Dollar", "অস্ট্রেলিয়ান ডলার", "A$", "🇦🇺"),
        CurrencyItem("CNY", "Chinese Yuan", "চীনা ইউয়ান", "¥", "🇨🇳"),
        CurrencyItem("JPY", "Japanese Yen", "জাপানি ইয়েন", "¥", "🇯🇵"),
        CurrencyItem("VND", "Vietnamese Dong", "ভিয়েতনামি ডং", "₫", "🇻🇳"),
        CurrencyItem("THB", "Thai Baht", "থাই বাত", "฿", "🇹🇭"),
        CurrencyItem("IDR", "Indonesian Rupiah", "ইন্দোনেশিয়ান রূপিয়া", "Rp", "🇮🇩")
    )

    fun getCurrency(code: String): CurrencyItem {
        return supportedCurrencies.find { it.code.equals(code, ignoreCase = true) }
            ?: supportedCurrencies[0]
    }

    private val formatter = DecimalFormat("#,##,##0.00")
    private val integerFormatter = DecimalFormat("#,##,##0")

    fun format(amount: Double, symbol: String = "৳", showDecimals: Boolean = false): String {
        val formattedNum = if (showDecimals || amount % 1.0 != 0.0) {
            formatter.format(amount)
        } else {
            integerFormatter.format(amount)
        }
        return "$symbol$formattedNum"
    }

    fun toBanglaDigits(input: String): String {
        val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = StringBuilder()
        for (char in input) {
            if (char in '0'..'9') {
                sb.append(banglaDigits[char - '0'])
            } else {
                sb.append(char)
            }
        }
        return sb.toString()
    }
}
