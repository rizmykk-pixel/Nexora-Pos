package com.nexova.pos.core.data.inventory

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "inventory_balances")
data class InventoryBalanceEntity(
    @PrimaryKey val productId: String,
    val variantId: String = "DEFAULT",
    val outletId: String,
    val quantity: Double,
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBalance(balance: InventoryBalanceEntity)

    @Query("SELECT * FROM inventory_balances WHERE outletId = :outletId")
    fun observeBalances(outletId: String): Flow<List<InventoryBalanceEntity>>

    @Query("SELECT * FROM inventory_balances WHERE productId = :productId AND variantId = :variantId AND outletId = :outletId")
    suspend fun getBalance(productId: String, variantId: String, outletId: String): InventoryBalanceEntity?
}
