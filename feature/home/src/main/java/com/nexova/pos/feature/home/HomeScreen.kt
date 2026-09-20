package com.nexova.pos.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexova.pos.core.designsystem.components.AdaptiveScaffold
import com.nexova.pos.core.designsystem.components.NexovaButton
import com.nexova.pos.core.designsystem.components.NexovaCard
import com.nexova.pos.core.domain.WindowSizeClass

@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel = viewModel(),
    onCatalogClick: () -> Unit = {},
    onPosClick: () -> Unit = {},
    onReportsClick: () -> Unit = {}
) {
    val uiState by homeViewModel.uiState.collectAsState()
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val windowSizeClass = remember(maxWidth) { classifyWindow(maxWidth.value) }
        AdaptiveShell(
            windowSizeClass = windowSizeClass, 
            uiState = uiState,
            onCatalogClick = onCatalogClick,
            onPosClick = onPosClick,
            onReportsClick = onReportsClick
        )
    }
}

@Composable
private fun AdaptiveShell(
    windowSizeClass: WindowSizeClass, 
    uiState: HomeUiState,
    onCatalogClick: () -> Unit,
    onPosClick: () -> Unit,
    onReportsClick: () -> Unit
) {
    val destinations = listOf("Beranda", "POS", "Katalog", "Laporan")
    
    AdaptiveScaffold(
        windowSizeClass = windowSizeClass,
        topBar = {
            Text(
                "NEXOVA POS",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigation = { isExpanded ->
            if (isExpanded) {
                NavigationRail {
                    destinations.forEachIndexed { index, label ->
                        NavigationRailItem(
                            selected = index == 0,
                            onClick = { 
                                when (index) {
                                    1 -> onPosClick()
                                    2 -> onCatalogClick()
                                    3 -> onReportsClick()
                                }
                            },
                            label = { Text(label) },
                            icon = { Text(label.take(1)) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar {
                destinations.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = index == 0,
                        onClick = { 
                            when (index) {
                                1 -> onPosClick()
                                2 -> onCatalogClick()
                                3 -> onReportsClick()
                            }
                        },
                        label = { Text(label) },
                        icon = { Text(label.take(1)) }
                    )
                }
            }
        }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(uiState.outlet) })
                AssistChip(onClick = {}, label = { Text(uiState.role.name.lowercase().replaceFirstChar { it.uppercase() }) })
                AssistChip(onClick = {}, label = { Text(if (uiState.isOffline) "Offline" else "Ready") })
            }
            
            Divider()
            
            val contentModifier = if (windowSizeClass == WindowSizeClass.EXPANDED) {
                Modifier.weight(1f)
            } else {
                Modifier.fillMaxWidth()
            }

            if (windowSizeClass == WindowSizeClass.EXPANDED) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ActionCard("Mulai Penjualan", "Buka keranjang baru", contentModifier, onPosClick)
                    ActionCard("Laporan Analitik", "Lihat performa bisnis", contentModifier, onReportsClick)
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ActionCard("Mulai Penjualan", "Buka keranjang baru", contentModifier, onPosClick)
                    ActionCard("Laporan Analitik", "Lihat performa bisnis", contentModifier, onReportsClick)
                }
            }
            
            Text("Ruang Kerja Offline-First", style = MaterialTheme.typography.titleMedium)
            Text(
                "Pekerjaan lokal Anda tetap tersedia saat koneksi berubah. Rekonsiliasi server dilakukan secara otomatis di latar belakang.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String, 
    subtitle: String, 
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    NexovaCard(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        NexovaButton(onClick = onClick) { Text("Buka") }
    }
}

fun classifyWindow(widthDp: Float): WindowSizeClass = when {
    widthDp < 600f -> WindowSizeClass.COMPACT
    widthDp < 840f -> WindowSizeClass.MEDIUM
    else -> WindowSizeClass.EXPANDED
}
