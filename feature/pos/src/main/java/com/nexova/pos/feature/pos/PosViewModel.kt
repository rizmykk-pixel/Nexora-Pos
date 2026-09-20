package com.nexova.pos.feature.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexova.pos.core.data.pos.PosRepository
import com.nexova.pos.core.domain.Quantity
import com.nexova.pos.core.domain.catalog.Product
import com.nexova.pos.core.domain.pos.Cart
import com.nexova.pos.core.domain.pos.CartItem
import com.nexova.pos.core.hardware.PosPrinter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.nexova.pos.core.network.payment.PaymentProvider
import com.nexova.pos.core.network.payment.PaymentResponse
import com.nexova.pos.core.network.payment.PaymentStatus
import com.nexova.pos.core.domain.pos.PaymentMethod
import com.nexova.pos.core.domain.pos.PaymentRequest

@HiltViewModel
class PosViewModel @Inject constructor(
    private val posRepository: PosRepository,
    private val printer: PosPrinter,
    private val paymentProvider: PaymentProvider
) : ViewModel() {

    private val _cart = MutableStateFlow(Cart())
    val cart: StateFlow<Cart> = _cart.asStateFlow()

    private val _paymentState = MutableStateFlow<PaymentResponse?>(null)
    val paymentState: StateFlow<PaymentResponse?> = _paymentState.asStateFlow()

    private val _paymentError = MutableStateFlow<String?>(null)
    val paymentError: StateFlow<String?> = _paymentError.asStateFlow()

    fun addToCart(product: Product) {
        _cart.update { currentCart ->
            val existingItem = currentCart.items.find { it.product.id == product.id && it.variant == null }
            if (existingItem != null) {
                val updatedItem = existingItem.copy(
                    quantity = Quantity.of(existingItem.quantity.value.toDouble() + 1.0)
                )
                currentCart.copy(items = currentCart.items.map { if (it.id == existingItem.id) updatedItem else it })
            } else {
                val newItem = CartItem(
                    product = product,
                    quantity = Quantity.of(1.0),
                    unitPrice = product.basePrice
                )
                currentCart.copy(items = currentCart.items + newItem)
            }
        }
    }

    fun checkout(businessId: String, outletId: String) {
        viewModelScope.launch {
            _paymentError.value = null
            if (businessId.isBlank() || outletId.isBlank()) {
                _paymentError.value = "Bisnis dan outlet harus dipilih sebelum checkout"
                return@launch
            }
            try {
                val orderId = posRepository.createOrder(_cart.value, businessId, outletId)
                val response = paymentProvider.createPayment(
                    PaymentRequest(
                        transactionId = orderId,
                        amount = _cart.value.total,
                        method = PaymentMethod.QRIS
                    )
                )
                _paymentState.value = response
            } catch (error: Exception) {
                _paymentError.value = error.message ?: "Pembayaran tidak dapat diproses"
            }
        }
    }

    fun completeTransaction() {
        _cart.value = Cart()
        _paymentState.value = null
        _paymentError.value = null
    }
}
