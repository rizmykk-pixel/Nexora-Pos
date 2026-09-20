package com.nexova.pos.core.model

import kotlinx.serialization.Serializable

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
