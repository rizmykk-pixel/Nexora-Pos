package com.nexova.pos.core.domain.report

import com.nexova.pos.core.domain.Money
import kotlinx.serialization.Serializable

/**
 * Rule 627: KPI Data Model.
 */
@Serializable
data class KpiValue(
    val title: String,
    val value: String,
    val subValue: String? = null,
    val trend: Double? = null, // Percentage change
    val periodLabel: String? = null
)

@Serializable
data class SalesSummary(
    val grossSales: Money,
    val netSales: Money,
    val totalOrders: Int,
    val averageOrderValue: Money,
    val topProducts: List<ProductSales> = emptyList()
)

@Serializable
data class ProductSales(
    val productId: String,
    val productName: String,
    val quantity: Double,
    val totalSales: Money
)
