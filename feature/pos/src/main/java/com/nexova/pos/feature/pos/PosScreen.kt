package com.nexova.pos.feature.pos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexova.pos.core.designsystem.components.NexovaButton
import com.nexova.pos.core.designsystem.components.NexovaCard
import com.nexova.pos.core.domain.WindowSizeClass
import com.nexova.pos.core.domain.catalog.Product
import com.nexova.pos.core.domain.pos.Cart
import com.nexova.pos.feature.catalog.list.ProductListViewModel
import com.nexova.pos.feature.catalog.list.ProductListUiState
import com.nexova.pos.feature.pos.payment.PaymentScreen

@Composable
fun PosRoute(
    posViewModel: PosViewModel,
    catalogViewModel: ProductListViewModel,
    windowSizeClass: WindowSizeClass,
    businessId: String,
    outletId: String
) {
    val cart by posViewModel.cart.collectAsState()
    val catalogState by catalogViewModel.uiState.collectAsState()
    val paymentState by posViewModel.paymentState.collectAsState()
    val paymentError by posViewModel.paymentError.collectAsState()

    if (paymentState != null) {
        PaymentScreen(
            paymentResponse = paymentState,
            onPaymentComplete = { posViewModel.completeTransaction() }
        )
    } else {
        PosScreen(
            cart = cart,
            catalogState = catalogState,
            windowSizeClass = windowSizeClass,
            paymentError = paymentError,
            onProductClick = { posViewModel.addToCart(it) },
            onCheckoutClick = { posViewModel.checkout(businessId, outletId) }
        )
    }
}

@Composable
private fun PosScreen(
    cart: Cart,
    catalogState: ProductListUiState,
    windowSizeClass: WindowSizeClass,
    paymentError: String?,
    onProductClick: (Product) -> Unit,
    onCheckoutClick: () -> Unit
) {
    val isTablet = windowSizeClass != WindowSizeClass.COMPACT

    Row(modifier = Modifier.fillMaxSize()) {
        // Catalog Section
        Box(modifier = Modifier.weight(if (isTablet) 0.6f else 1f)) {
            when (catalogState) {
                is ProductListUiState.Success -> {
                    LazyColumn {
                        items(catalogState.products) { product ->
                            NexovaCard(
                                onClick = { onProductClick(product) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Rp ${product.basePrice.minorUnits}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
                else -> { /* Loading/Error */ }
            }
        }

        // Cart Section (Tablet only or as a separate navigation on Phone)
        if (isTablet) {
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(16.dp)
            ) {
                if (paymentError != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = paymentError,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                Text(text = "Keranjang", style = MaterialTheme.typography.headlineSmall)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cart.items) { item ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(text = "${item.product.name} x${item.quantity.value}", modifier = Modifier.weight(1f))
                            Text(text = "Rp ${item.total.minorUnits}")
                        }
                    }
                }
                Text(text = "Total: Rp ${cart.total.minorUnits}", style = MaterialTheme.typography.titleLarge)
                NexovaButton(
                    onClick = onCheckoutClick,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Bayar Sekarang")
                }
            }
        }
    }
}
