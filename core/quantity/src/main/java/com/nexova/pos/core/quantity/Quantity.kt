package com.nexova.pos.core.quantity

import com.nexova.pos.core.common.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Represents a quantity with fixed decimal precision.
 * Rule 107: Use decimal-safe representation.
 */
@Serializable
@JvmInline
value class Quantity(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal
) {
    companion object {
        private const val DEFAULT_SCALE = 3
        
        fun of(value: BigDecimal): Quantity = 
            Quantity(value.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP))
            
        fun of(value: Double): Quantity = of(BigDecimal.valueOf(value))
        
        val ZERO = of(BigDecimal.ZERO)
    }
    
    operator fun plus(other: Quantity) = of(value + other.value)
    operator fun minus(other: Quantity) = of(value - other.value)
}
