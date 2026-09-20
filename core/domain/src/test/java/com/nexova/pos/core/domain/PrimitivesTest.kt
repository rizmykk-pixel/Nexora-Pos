package com.nexova.pos.core.domain

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class PrimitivesTest {
    @Test
    fun moneyAddsMinorUnitsExactly() {
        assertEquals(1250L, (Money.ofMinorUnits(1000) + Money.ofMinorUnits(250)).minorUnits)
    }

    @Test
    fun moneyMultiplication() {
        assertEquals(3000L, (Money.ofMinorUnits(1000) * 3).minorUnits)
    }

    @Test
    fun quantityIsNormalizedToThreeDecimals() {
        assertEquals(BigDecimal("1.235"), Quantity.of(BigDecimal("1.2346")).value)
        assertEquals(BigDecimal("1.000"), Quantity.of(1.0).value)
    }

    @Test
    fun quantityArithmetic() {
        val q1 = Quantity.of(1.5)
        val q2 = Quantity.of(2.5)
        assertEquals(BigDecimal("4.000"), (q1 + q2).value)
        assertEquals(BigDecimal("1.000"), (q2 - q1).value)
    }

    @Test
    fun moneyAmountDefaultCurrency() {
        assertEquals("IDR", MoneyAmount(Money.ofMinorUnits(1000)).currencyCode)
    }
}
