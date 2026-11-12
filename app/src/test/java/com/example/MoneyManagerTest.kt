package com.example

import com.example.data.model.PersonEntity
import com.example.data.model.PersonType
import com.example.util.CurrencyHelper
import com.example.util.Localization
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoneyManagerTest {

    @Test
    fun testCurrencyFormatting() {
        val bdt = CurrencyHelper.format(25000.0, "৳")
        assertEquals("৳25,000", bdt)

        val usd = CurrencyHelper.format(1250.50, "$", showDecimals = true)
        assertEquals("$1,250.50", usd)
    }

    @Test
    fun testLocalization() {
        val balanceBn = Localization.get("current_balance", Localization.LANG_BN)
        assertEquals("মোট ব্যালেন্স", balanceBn)

        val balanceEn = Localization.get("current_balance", Localization.LANG_EN)
        assertEquals("Current Balance", balanceEn)

        val toPayBn = Localization.get("to_pay", Localization.LANG_BN)
        assertEquals("দিতে হবে", toPayBn)

        val toReceiveBn = Localization.get("to_receive", Localization.LANG_BN)
        assertEquals("পাবো", toReceiveBn)
    }

    @Test
    fun testPersonCalculation() {
        val person = PersonEntity(
            name = "Rahim",
            type = PersonType.OWE,
            totalAmount = 10000.0,
            paidAmount = 4000.0
        )
        assertEquals(6000.0, person.remainingAmount, 0.001)
        assertEquals(40.0f, person.progressPercent, 0.001f)
    }

    @Test
    fun testBiometricSettings() {
        val settings = com.example.data.model.UserSettingsEntity(isBiometricEnabled = true)
        assertTrue(settings.isBiometricEnabled)
        assertEquals("বায়োমেট্রিক নিরাপত্তা", Localization.get("biometric_security", Localization.LANG_BN))
        assertEquals("Biometric Security", Localization.get("biometric_security", Localization.LANG_EN))
    }
}
