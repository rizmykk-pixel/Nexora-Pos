package com.nexova.pos.core.network.payment

import com.nexova.pos.core.domain.pos.PaymentMethod
import com.nexova.pos.core.domain.pos.PaymentRequest
import kotlinx.coroutines.flow.Flow

/**
 * Rule 167: Payment Provider Interface.
 */

enum class PaymentStatus {
    INITIATED, PENDING, AUTHORIZED, PAID, FAILED, EXPIRED, CANCELLED, UNKNOWN
}

data class PaymentResponse(
    val paymentId: String,
    val transactionId: String,
    val status: PaymentStatus,
    val qrCodeData: String? = null,
    val providerReference: String? = null
)

interface PaymentProvider {
    suspend fun createPayment(request: PaymentRequest): PaymentResponse
    suspend fun getStatus(paymentId: String): PaymentStatus
    suspend fun cancelPayment(paymentId: String): Boolean
    suspend fun refundPayment(paymentId: String, amountMinorUnits: Long): Boolean
}
