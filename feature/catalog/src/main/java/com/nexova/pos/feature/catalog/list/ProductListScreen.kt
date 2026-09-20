package com.nexova.pos.feature.catalog.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nexova.pos.core.designsystem.components.AdaptiveScaffold
import com.nexova.pos.core.designsystem.components.NexovaCard
import com.nexova.pos.core.domain.WindowSizeClass
import com.nexova.pos.core.domain.catalog.Category
import com.nexova.pos.core.domain.catalog.Product

@Composable
fun ProductListRoute(
    viewModel: ProductListViewModel,
    windowSizeClass: WindowSizeClass,
    onProductClick: (Product) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    ProductListScreen(
        uiState = uiState,
        windowSizeClass = windowSizeClass,
        onCategorySelect = viewModel::selectCategory,
        onProductClick = onProductClick
    )
}

@Composable
private fun ProductListScreen(
    uiState: ProductListUiState,
    windowSizeClass: WindowSizeClass,
    onCategorySelect: (String?) -> Unit,
    onProductClick: (Product) -> Unit
) {
    AdaptiveScaffold(
        windowSizeClass = windowSizeClass,
        topBar = {
            Text(
                "Katalog Produk",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        navigation = { /* Sidebar if needed */ },
        content = {
            when (uiState) {
                is ProductListUiState.Loading -> CircularProgressIndicator()
                is ProductListUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        CategoryFilters(
                            categories = uiState.categories,
                            selectedId = uiState.selectedCategoryId,
                            onSelect = onCategorySelect
                        )
                        
                        ProductGrid(
                            products = uiState.products,
                            windowSizeClass = windowSizeClass,
                            onProductClick = onProductClick
                        )
                    }
                }
                is ProductListUiState.Error -> Text(uiState.message)
            }
        }
    )
}

@Composable
private fun CategoryFilters(
    categories: List<Category>,
    selectedId: String?,
    onSelect: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedId == null,
                onClick = { onSelect(null) },
                label = { Text("Semua") }
            )
        }
        items(categories) { category ->
            FilterChip(
                selected = selectedId == category.id,
                onClick = { onSelect(category.id) },
                label = { Text(category.name) }
            )
        }
    }
}

@Composable
private fun ProductGrid(
    products: List<Product>,
    windowSizeClass: WindowSizeClass,
    onProductClick: (Product) -> Unit
) {
    val columns = when (windowSizeClass) {
        WindowSizeClass.COMPACT -> 2
        WindowSizeClass.MEDIUM -> 3
        WindowSizeClass.EXPANDED -> 4
        else -> 5
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(products) { product ->
            ProductItem(product = product, onClick = { onProductClick(product) })
        }
    }
}

@Composable
private fun ProductItem(product: Product, onClick: () -> Unit) {
    NexovaCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Rp ${product.basePrice.minorUnits}", // Simple format for now
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
