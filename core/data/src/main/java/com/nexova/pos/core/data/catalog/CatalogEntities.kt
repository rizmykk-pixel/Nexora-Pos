package com.nexova.pos.core.data.catalog

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val parentId: String?
)

@Entity(tableName = "tax_rules")
data class TaxRuleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val ratePercentage: Double,
    val isInclusive: Boolean
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val name: String,
    val sku: String?,
    val barcode: String?,
    val unit: String,
    val basePriceMinorUnits: Long,
    val taxRuleId: String?,
    val imageUrl: String?,
    val isActive: Boolean,
    val variantsJson: String // Serialized List<ProductVariant>
)

@Dao
interface CatalogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategories(categories: List<CategoryEntity>): List<Long>

    @Query("SELECT * FROM categories")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTaxRules(rules: List<TaxRuleEntity>): List<Long>

    @Query("SELECT * FROM tax_rules")
    fun observeTaxRules(): Flow<List<TaxRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProducts(products: List<ProductEntity>): List<Long>

    @Query("SELECT * FROM products WHERE isActive = 1")
    fun observeActiveProducts(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun observeProductsByCategory(categoryId: String): Flow<List<ProductEntity>>
}
