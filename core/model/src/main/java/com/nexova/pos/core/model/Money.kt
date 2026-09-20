package com.nexova.pos.core.model

import kotlinx.serialization.Serializable

/**
 * Represents a monetary value in the smallest currency unit (e.g., Sen for IDR, though rarely used).
 * As per Nexova POS standards: Rp15.500 is stored as 15500.
 */
@Serializable
@JvmInline
value class Money(val amount: Long) {
    companion object {
        val ZERO = Money(0)
    }

    operator fun plus(other: Money) = Money(this.amount + other.amount)
    operator fun minus(other: Money) = Money(this.amount - other.amount)
    operator fun times(factor: Int) = Money(this.amount * factor)
    operator fun div(factor: Int) = Money(this.amount / factor)

    fun format(currencyCode: String = "IDR"): String {
        // Basic formatting for IDR, in production this would use NumberFormat
        return "Rp " + String.format("%,d", amount).replace(',', '.')
    }
}
