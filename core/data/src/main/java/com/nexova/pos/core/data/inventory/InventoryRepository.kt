package com.nexova.pos.core.data.inventory

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor(
    private val inventoryDao: InventoryDao
) {
    fun observeBalances(outletId: String): Flow<List<InventoryBalanceEntity>> {
        return inventoryDao.observeBalances(outletId)
    }

    suspend fun updateBalance(productId: String, variantId: String, outletId: String, newQuantity: Double) {
        val balance = InventoryBalanceEntity(
            productId = productId,
            variantId = variantId,
            outletId = outletId,
            quantity = newQuantity
        )
        inventoryDao.updateBalance(balance)
    }
}
