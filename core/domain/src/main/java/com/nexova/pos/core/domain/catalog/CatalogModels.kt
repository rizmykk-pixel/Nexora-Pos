package com.nexova.pos.core.domain.catalog

import com.nexova.pos.core.domain.Money
import com.nexova.pos.core.domain.Quantity
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String? = null,
    val parentId: String? = null
)

@Serializable
data class TaxRule(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val ratePercentage: Double,
    val isInclusive: Boolean = false
)

@Serializable
data class Product(
    val id: String = UUID.randomUUID().toString(),
    val categoryId: String,
    val name: String,
    val sku: String? = null,
    val barcode: String? = null,
    val unit: String = "pcs",
    val basePrice: Money,
    val taxRuleId: String? = null,
    val imageUrl: String? = null,
    val isActive: Boolean = true,
    val variants: List<ProductVariant> = emptyList()
)

@Serializable
data class ProductVariant(
    val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val name: String,
    val sku: String? = null,
    val barcode: String? = null,
    val priceAdjustment: Money = Money.ZERO,
    val isActive: Boolean = true
)
