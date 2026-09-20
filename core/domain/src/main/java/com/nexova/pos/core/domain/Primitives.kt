package com.nexova.pos.core.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.util.Currency
import java.util.UUID

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeLong(value.toEpochMilli())
    override fun deserialize(decoder: Decoder): Instant = Instant.ofEpochMilli(decoder.decodeLong())
}

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

object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: BigDecimal) = encoder.encodeString(value.toPlainString())
    override fun deserialize(decoder: Decoder): BigDecimal = BigDecimal(decoder.decodeString())
}

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

/**
 * Rule 82: Tenancy and Business Context.
 */
@Serializable
data class BusinessContext(
    val userId: String,
    val businessId: String,
    val outletId: String,
    val deviceId: String,
    val registerId: String? = null
)

@Serializable
data class SyncOperation(
    val idempotencyKey: String = UUID.randomUUID().toString(),
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant = Instant.now(),
    val payload: String,
    val state: SyncState = SyncState.PENDING,
    val retryCount: Int = 0,
    val correlationId: String? = null
)

enum class SyncState { PENDING, IN_FLIGHT, ACKNOWLEDGED, FAILED, CONFLICT }

enum class UserRole { 
    OWNER, 
    HQ_ADMIN, 
    REGIONAL_MANAGER, 
    ENTITY_MANAGER, 
    OUTLET_MANAGER, 
    SUPERVISOR, 
    CASHIER, 
    STAFF, 
    ACCOUNTANT, 
    WAREHOUSE, 
    PROCUREMENT, 
    AUDITOR, 
    SUPPORT 
}

enum class WindowSizeClass { COMPACT, MEDIUM, EXPANDED, LARGE, EXTRA_LARGE }
