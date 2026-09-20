package com.nexova.pos.feature.catalog.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexova.pos.core.data.catalog.CatalogRepository
import com.nexova.pos.core.domain.catalog.Category
import com.nexova.pos.core.domain.catalog.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: CatalogRepository
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    
    val uiState: StateFlow<ProductListUiState> = combine(
        repository.observeActiveProducts(),
        repository.observeCategories(),
        _selectedCategoryId
    ) { products, categories, selectedId ->
        val filteredProducts = if (selectedId == null) {
            products
        } else {
            products.filter { it.categoryId == selectedId }
        }
        
        ProductListUiState.Success(
            products = filteredProducts,
            categories = categories,
            selectedCategoryId = selectedId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductListUiState.Loading
    )

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }
}

sealed interface ProductListUiState {
    object Loading : ProductListUiState
    data class Success(
        val products: List<Product>,
        val categories: List<Category>,
        val selectedCategoryId: String?
    ) : ProductListUiState
    data class Error(val message: String) : ProductListUiState
}
