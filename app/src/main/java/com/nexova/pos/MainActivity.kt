package com.nexova.pos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexova.pos.core.designsystem.NexovaTheme
import com.nexova.pos.core.domain.catalog.Product
import com.nexova.pos.feature.auth.BusinessSelectorRoute
import com.nexova.pos.feature.auth.LoginRoute
import com.nexova.pos.feature.auth.LoginUiState
import com.nexova.pos.feature.auth.LoginViewModel
import com.nexova.pos.feature.catalog.detail.ProductDetailRoute
import com.nexova.pos.feature.catalog.list.ProductListRoute
import com.nexova.pos.feature.catalog.list.ProductListViewModel
import com.nexova.pos.feature.home.HomeRoute
import com.nexova.pos.feature.home.classifyWindow
import com.nexova.pos.feature.pos.PosRoute
import com.nexova.pos.feature.pos.PosViewModel
import com.nexova.pos.feature.reports.ReportRoute
import com.nexova.pos.feature.reports.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint

enum class Screen { Login, BusinessSelector, Home, Catalog, ProductDetail, Pos, Reports }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NexovaTheme {
                MainShell()
            }
        }
    }
}

@Composable
fun MainShell() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val windowSizeClass = remember(maxWidth) { classifyWindow(maxWidth.value) }
        val loginViewModel: LoginViewModel = hiltViewModel()
        val authState by loginViewModel.uiState.collectAsState()
        
        var selectedBusiness by remember { mutableStateOf<String?>(null) }
        var currentScreen by remember { mutableStateOf(Screen.Login) }
        var selectedProduct by remember { mutableStateOf<Product?>(null) }

        LaunchedEffect(authState, selectedBusiness) {
            if (currentScreen == Screen.Login || currentScreen == Screen.BusinessSelector) {
                currentScreen = when {
                    authState is LoginUiState.Success && selectedBusiness == null -> Screen.BusinessSelector
                    authState is LoginUiState.Success && selectedBusiness != null -> Screen.Home
                    else -> Screen.Login
                }
            }
        }

        when (currentScreen) {
            Screen.Login -> {
                LoginRoute(
                    viewModel = loginViewModel,
                    windowSizeClass = windowSizeClass,
                    onLoginSuccess = { /* Handled by LaunchedEffect */ }
                )
            }
            Screen.BusinessSelector -> {
                BusinessSelectorRoute(
                    windowSizeClass = windowSizeClass,
                    onBusinessSelected = { selectedBusiness = it }
                )
            }
            Screen.Home -> {
                HomeRoute(
                    onCatalogClick = { currentScreen = Screen.Catalog },
                    onPosClick = { currentScreen = Screen.Pos },
                    onReportsClick = { currentScreen = Screen.Reports }
                )
            }
            Screen.Catalog -> {
                val catalogViewModel: ProductListViewModel = hiltViewModel()
                ProductListRoute(
                    viewModel = catalogViewModel,
                    windowSizeClass = windowSizeClass,
                    onProductClick = { 
                        selectedProduct = it
                        currentScreen = Screen.ProductDetail
                    }
                )
            }
            Screen.ProductDetail -> {
                selectedProduct?.let { product ->
                    ProductDetailRoute(
                        product = product,
                        windowSizeClass = windowSizeClass,
                        onBack = { currentScreen = Screen.Pos }
                    )
                }
            }
            Screen.Pos -> {
                val posViewModel: PosViewModel = hiltViewModel()
                val catalogViewModel: ProductListViewModel = hiltViewModel()
                PosRoute(
                    posViewModel = posViewModel,
                    catalogViewModel = catalogViewModel,
                    windowSizeClass = windowSizeClass,
                    businessId = selectedBusiness ?: "DEFAULT",
                    outletId = ""
                )
            }
            Screen.Reports -> {
                val reportViewModel: ReportViewModel = hiltViewModel()
                ReportRoute(
                    viewModel = reportViewModel,
                    windowSizeClass = windowSizeClass
                )
            }
        }
    }
}
