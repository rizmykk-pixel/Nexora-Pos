package com.nexova.pos.core.domain.pos

import com.nexova.pos.core.domain.Money
import com.nexova.pos.core.domain.Quantity
import com.nexova.pos.core.domain.catalog.Product
import com.nexova.pos.core.domain.catalog.ProductVariant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val product: Product,
    val variant: ProductVariant? = null,
    val quantity: Quantity,
    val unitPrice: Money,
    val discount: Money = Money.ZERO,
    val notes: String? = null
) {
    val subtotal: Money get() = (unitPrice.minorUnits * quantity.value.toDouble()).toLong().let { Money(it) }
    val total: Money get() = Money(subtotal.minorUnits - discount.minorUnits)
}

@Serializable
data class Cart(
    val items: List<CartItem> = emptyList(),
    val customerId: String? = null
) {
    val subtotal: Money get() = Money(items.sumOf { it.subtotal.minorUnits })
    val discount: Money get() = Money(items.sumOf { it.discount.minorUnits })
    val total: Money get() = Money(items.sumOf { it.total.minorUnits })
}

enum class PaymentMethod {
    CASH, QRIS, DEBIT, CREDIT_CARD, EWALLET
}

@Serializable
data class PaymentRequest(
    val transactionId: String,
    val amount: Money,
    val method: PaymentMethod,
    val idempotencyKey: String = UUID.randomUUID().toString()
)
