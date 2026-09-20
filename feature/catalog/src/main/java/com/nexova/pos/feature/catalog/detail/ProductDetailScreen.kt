package com.nexova.pos.feature.catalog.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nexova.pos.core.designsystem.components.AdaptiveScaffold
import com.nexova.pos.core.designsystem.components.NexovaButton
import com.nexova.pos.core.designsystem.components.NexovaCard
import com.nexova.pos.core.domain.WindowSizeClass
import com.nexova.pos.core.domain.catalog.Product
import com.nexova.pos.core.domain.catalog.ProductVariant

@Composable
fun ProductDetailRoute(
    product: Product,
    windowSizeClass: WindowSizeClass,
    onBack: () -> Unit
) {
    ProductDetailScreen(
        product = product,
        windowSizeClass = windowSizeClass,
        onBack = onBack
    )
}

@Composable
private fun ProductDetailScreen(
    product: Product,
    windowSizeClass: WindowSizeClass,
    onBack: () -> Unit
) {
    AdaptiveScaffold(
        windowSizeClass = windowSizeClass,
        topBar = {
            Text(
                "Detail Produk",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        navigation = { /* Back button if needed */ },
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Rp ${product.basePrice.minorUnits}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "SKU: ${product.sku ?: "-"}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    
                    Text(
                        text = "Varian",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                
                items(product.variants) { variant ->
                    VariantItem(variant = variant)
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    NexovaButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Kembali")
                    }
                }
            }
        }
    )
}

@Composable
private fun VariantItem(variant: ProductVariant) {
    NexovaCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = variant.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "SKU: ${variant.sku ?: "-"}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "+ Rp ${variant.priceAdjustment.minorUnits}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
