package com.nexova.pos.feature.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexova.pos.core.designsystem.components.NexovaCard
import com.nexova.pos.core.domain.WindowSizeClass
import com.nexova.pos.core.domain.report.KpiValue

@Composable
fun ReportRoute(
    viewModel: ReportViewModel,
    windowSizeClass: WindowSizeClass
) {
    val kpis by viewModel.kpiState.collectAsState()
    
    ReportScreen(
        kpis = kpis,
        windowSizeClass = windowSizeClass
    )
}

@Composable
private fun ReportScreen(
    kpis: List<KpiValue>,
    windowSizeClass: WindowSizeClass
) {
    val isTablet = windowSizeClass != WindowSizeClass.COMPACT

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Laporan & Analitik", style = MaterialTheme.typography.headlineMedium)
        }

        item {
            if (isTablet) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    kpis.forEach { kpi ->
                        KpiCard(kpi, modifier = Modifier.weight(1f))
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    kpis.forEach { kpi ->
                        KpiCard(kpi, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
        
        // Rule 629: Table for Tablet, List for Phone
        item {
            Text("Performa Produk", style = MaterialTheme.typography.titleLarge)
        }
        
        // Placeholder for charts and tables
    }
}

@Composable
private fun KpiCard(kpi: KpiValue, modifier: Modifier = Modifier) {
    NexovaCard(modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = kpi.title, style = MaterialTheme.typography.labelLarge)
            Text(text = kpi.value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            kpi.subValue?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = if ((kpi.trend ?: 0.0) >= 0.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            }
        }
    }
}
