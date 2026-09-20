package com.nexova.pos.feature.home

import androidx.lifecycle.ViewModel
import com.nexova.pos.core.domain.UserRole
import com.nexova.pos.core.domain.WindowSizeClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val role: UserRole = UserRole.CASHIER, 
    val outlet: String = "Outlet Utama", 
    val pendingSync: Int = 0, 
    val isOffline: Boolean = false
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun setWindowClass(windowClass: WindowSizeClass) { 
        // Logic for layout-dependent state can be added here
    }
}
