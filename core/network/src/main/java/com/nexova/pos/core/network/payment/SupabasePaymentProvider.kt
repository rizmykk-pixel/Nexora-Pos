package com.nexova.pos.core.network.payment

import com.nexova.pos.core.domain.pos.PaymentRequest
import io.github.jan.supabase.SupabaseClient
import com.nexova.pos.core.network.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@Singleton
class SupabasePaymentProvider @Inject constructor(
    private val client: SupabaseClient
) : PaymentProvider {

    private val json = Json { ignoreUnknownKeys = true }
    
    private val httpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
        
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }
        
        builder.build()
    }

    private fun getBackendUrl(): String {
        return BuildConfig.BACKEND_URL
    }

    override suspend fun createPayment(request: PaymentRequest): PaymentResponse {
        val backendUrl = getBackendUrl()
        val requestBody = buildJsonObject {
            put("orderId", JsonPrimitive(request.transactionId))
            put("grossAmount", JsonPrimitive(request.amount.minorUnits))
        }.toString()
        
        val httpRequest = Request.Builder()
            .url("$backendUrl/api/v1/payments")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()
        
        val response = httpClient.newCall(httpRequest).execute()
        val responseBody = response.body?.string() ?: throw Exception("Empty response")
        
        if (!response.isSuccessful) {
            throw Exception("Payment creation failed: ${response.code} - $responseBody")
        }
        
        val jsonResponse = json.parseToJsonElement(responseBody) as JsonObject
        val status = when (jsonResponse["status"]?.toString()?.replace("\"", "")) {
            "pending" -> PaymentStatus.PENDING
            "paid" -> PaymentStatus.PAID
            "initiated" -> PaymentStatus.INITIATED
            "authorized" -> PaymentStatus.AUTHORIZED
            "failed" -> PaymentStatus.FAILED
            "expired" -> PaymentStatus.EXPIRED
            "cancelled" -> PaymentStatus.CANCELLED
            else -> PaymentStatus.UNKNOWN
        }
        
        return PaymentResponse(
            paymentId = jsonResponse["paymentId"]?.toString()?.replace("\"", "") ?: request.transactionId,
            transactionId = jsonResponse["transactionId"]?.toString()?.replace("\"", "") ?: request.transactionId,
            status = status,
            qrCodeData = jsonResponse["qrCodeUrl"]?.toString()?.replace("\"", "")
        )
    }

    override suspend fun getStatus(paymentId: String): PaymentStatus {
        val backendUrl = getBackendUrl()
        val httpRequest = Request.Builder()
            .url("$backendUrl/api/v1/payments/$paymentId")
            .get()
            .build()
        
        val response = httpClient.newCall(httpRequest).execute()
        val responseBody = response.body?.string() ?: throw Exception("Empty response")
        
        if (!response.isSuccessful) {
            throw Exception("Payment status check failed: ${response.code} - $responseBody")
        }
        
        val jsonResponse = json.parseToJsonElement(responseBody) as JsonObject
        return when (jsonResponse["status"]?.toString()?.replace("\"", "")) {
            "pending" -> PaymentStatus.PENDING
            "paid" -> PaymentStatus.PAID
            "initiated" -> PaymentStatus.INITIATED
            "authorized" -> PaymentStatus.AUTHORIZED
            "failed" -> PaymentStatus.FAILED
            "expired" -> PaymentStatus.EXPIRED
            "cancelled" -> PaymentStatus.CANCELLED
            else -> PaymentStatus.UNKNOWN
        }
    }

    override suspend fun cancelPayment(paymentId: String): Boolean {
        // Not implemented in current backend
        throw UnsupportedOperationException(
            "Payment cancellation is not supported by the current backend implementation."
        )
    }

    override suspend fun refundPayment(paymentId: String, amountMinorUnits: Long): Boolean {
        // Not implemented in current backend
        throw UnsupportedOperationException(
            "Payment refund is not supported by the current backend implementation."
        )
    }
}
