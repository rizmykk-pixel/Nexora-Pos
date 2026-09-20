package com.nexova.pos.core.data.catalog

import com.nexova.pos.core.domain.Money
import com.nexova.pos.core.domain.catalog.Category
import com.nexova.pos.core.domain.catalog.Product
import com.nexova.pos.core.domain.catalog.ProductVariant
import com.nexova.pos.core.domain.catalog.TaxRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepository @Inject constructor(
    private val catalogDao: CatalogDao
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun observeCategories(): Flow<List<Category>> =
        catalogDao.observeCategories().map { it.map { entity -> entity.toDomain() } }

    fun observeTaxRules(): Flow<List<TaxRule>> =
        catalogDao.observeTaxRules().map { it.map { entity -> entity.toDomain() } }

    fun observeActiveProducts(): Flow<List<Product>> =
        catalogDao.observeActiveProducts().map { it.map { entity -> entity.toDomain() } }

    fun observeProductsByCategory(categoryId: String): Flow<List<Product>> =
        catalogDao.observeProductsByCategory(categoryId).map { it.map { entity -> entity.toDomain() } }

    suspend fun upsertProducts(products: List<Product>) {
        catalogDao.upsertProducts(products.map { it.toEntity() })
    }

    suspend fun upsertCategories(categories: List<Category>) {
        catalogDao.upsertCategories(categories.map { it.toEntity() })
    }

    suspend fun upsertTaxRules(rules: List<TaxRule>) {
        catalogDao.upsertTaxRules(rules.map { it.toEntity() })
    }

    private fun CategoryEntity.toDomain() = Category(id, name, description, parentId)
    private fun Category.toEntity() = CategoryEntity(id, name, description, parentId)

    private fun TaxRuleEntity.toDomain() = TaxRule(id, name, ratePercentage, isInclusive)
    private fun TaxRule.toEntity() = TaxRuleEntity(id, name, ratePercentage, isInclusive)

    private fun ProductEntity.toDomain() = Product(
        id = id,
        categoryId = categoryId,
        name = name,
        sku = sku,
        barcode = barcode,
        unit = unit,
        basePrice = Money.ofMinorUnits(basePriceMinorUnits),
        taxRuleId = taxRuleId,
        imageUrl = imageUrl,
        isActive = isActive,
        variants = json.decodeFromString(variantsJson)
    )

    private fun Product.toEntity() = ProductEntity(
        id = id,
        categoryId = categoryId,
        name = name,
        sku = sku,
        barcode = barcode,
        unit = unit,
        basePriceMinorUnits = basePrice.minorUnits,
        taxRuleId = taxRuleId,
        imageUrl = imageUrl,
        isActive = isActive,
        variantsJson = json.encodeToString(variants)
    )
}
