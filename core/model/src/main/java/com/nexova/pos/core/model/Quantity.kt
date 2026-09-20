package com.nexova.pos.core.model

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Represents a decimal-safe quantity for inventory, procurement, and sales.
 */
@Serializable
data class Quantity(
    val value: Double,
    val unit: String,
    val precision: Int = 2
) {
    fun toBigDecimal(): BigDecimal = BigDecimal.valueOf(value).setScale(precision, RoundingMode.HALF_UP)

    companion object {
        fun zero(unit: String) = Quantity(0.0, unit)
    }
}
