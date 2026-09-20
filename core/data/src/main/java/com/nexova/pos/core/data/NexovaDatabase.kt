package com.nexova.pos.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.nexova.pos.core.data.catalog.CatalogDao
import com.nexova.pos.core.data.catalog.CategoryEntity
import com.nexova.pos.core.data.catalog.ProductEntity
import com.nexova.pos.core.data.catalog.TaxRuleEntity

import com.nexova.pos.core.data.inventory.InventoryBalanceEntity
import com.nexova.pos.core.data.inventory.InventoryDao
import com.nexova.pos.core.data.pos.OrderEntity
import com.nexova.pos.core.data.pos.OrderItemEntity
import com.nexova.pos.core.data.pos.PosDao

@Database(
    entities = [
        SyncEntity::class,
        CategoryEntity::class,
        TaxRuleEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        InventoryBalanceEntity::class
    ],
    version = 5,
    exportSchema = true
)
abstract class NexovaDatabase : RoomDatabase() {
    abstract fun syncOperationDao(): SyncOperationDao
    abstract fun catalogDao(): CatalogDao
    abstract fun posDao(): PosDao
    abstract fun inventoryDao(): InventoryDao

    companion object {
        fun create(context: Context): NexovaDatabase =
            Room.databaseBuilder(context, NexovaDatabase::class.java, "nexova.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
