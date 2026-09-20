package com.nexova.pos.feature.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexova.pos.core.domain.Money
import com.nexova.pos.core.domain.report.KpiValue
import com.nexova.pos.core.domain.report.SalesSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {
    
    val kpiState: StateFlow<List<KpiValue>> = MutableStateFlow(
        listOf(
            KpiValue("Penjualan Kotor", "Rp 12.500.000", "+15% vs bln lalu", 0.15),
            KpiValue("Total Pesanan", "450", "+5% vs bln lalu", 0.05),
            KpiValue("Rata-rata Keranjang", "Rp 27.777", "-2% vs bln lalu", -0.02)
        )
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val salesSummary: StateFlow<SalesSummary?> = MutableStateFlow(
        SalesSummary(
            grossSales = Money(12500000),
            netSales = Money(11000000),
            totalOrders = 450,
            averageOrderValue = Money(27777)
        )
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
