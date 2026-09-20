package com.nexova.pos.core.money

import kotlinx.serialization.Serializable

/**
 * Represents money in the smallest currency unit (minor units).
 * Rule 106: Never use Float/Double for money. Use integer smallest unit.
 */
@Serializable
@JvmInline
value class Money(val minorUnits: Long) {
    companion object {
        val ZERO = Money(0L)
        fun ofMinorUnits(units: Long) = Money(units)
    }
    
    operator fun plus(other: Money) = Money(Math.addExact(minorUnits, other.minorUnits))
    operator fun minus(other: Money) = Money(Math.subtractExact(minorUnits, other.minorUnits))
    operator fun times(factor: Int) = Money(Math.multiplyExact(minorUnits, factor.toLong()))
}

@Serializable
data class MoneyAmount(
    val amount: Money,
    val currencyCode: String = "IDR"
)
