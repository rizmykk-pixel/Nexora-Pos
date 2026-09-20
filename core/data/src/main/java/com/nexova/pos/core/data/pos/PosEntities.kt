package com.nexova.pos.core.data.pos

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val businessId: String,
    val outletId: String,
    val customerId: String?,
    val subtotalMinorUnits: Long,
    val discountMinorUnits: Long,
    val totalMinorUnits: Long,
    val status: String, // PENDING, PAID, CANCELLED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val productId: String,
    val variantId: String?,
    val quantity: Double,
    val unitPriceMinorUnits: Long,
    val discountMinorUnits: Long
)

@Dao
interface PosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: String): OrderEntity?

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: String): List<OrderItemEntity>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun observeAllOrders(): Flow<List<OrderEntity>>
}
