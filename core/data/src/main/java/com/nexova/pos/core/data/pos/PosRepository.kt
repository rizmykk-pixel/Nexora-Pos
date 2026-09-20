package com.nexova.pos.core.data.pos

import com.nexova.pos.core.data.sync.OfflineInterceptor
import com.nexova.pos.core.domain.Money
import com.nexova.pos.core.domain.Quantity
import com.nexova.pos.core.domain.pos.Cart
import com.nexova.pos.core.domain.pos.CartItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PosRepository @Inject constructor(
    private val posDao: PosDao,
    private val offlineInterceptor: OfflineInterceptor
) {
    suspend fun createOrder(cart: Cart, businessId: String, outletId: String): String {
        val orderId = UUID.randomUUID().toString()
        val order = OrderEntity(
            id = orderId,
            businessId = businessId,
            outletId = outletId,
            customerId = cart.customerId,
            subtotalMinorUnits = cart.subtotal.minorUnits,
            discountMinorUnits = cart.discount.minorUnits,
            totalMinorUnits = cart.total.minorUnits,
            status = "PENDING"
        )
        
        val items = cart.items.map { item ->
            OrderItemEntity(
                id = UUID.randomUUID().toString(),
                orderId = orderId,
                productId = item.product.id,
                variantId = item.variant?.id,
                quantity = item.quantity.value.toDouble(),
                unitPriceMinorUnits = item.unitPrice.minorUnits,
                discountMinorUnits = item.discount.minorUnits
            )
        }

        posDao.insertOrder(order)
        posDao.insertOrderItems(items)

        // Rule 128: Save durable local operation.
        offlineInterceptor.interceptMutation(
            entityType = "ORDER",
            entityId = orderId,
            action = "CREATE",
            payload = Json.encodeToString(cart)
        )

        return orderId
    }
}
