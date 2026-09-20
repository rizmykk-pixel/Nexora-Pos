package com.nexova.pos.feature.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import com.nexova.pos.core.designsystem.components.AdaptiveScaffold
import com.nexova.pos.core.designsystem.components.NexovaCard
import com.nexova.pos.core.domain.WindowSizeClass

@Composable
fun BusinessSelectorRoute(
    windowSizeClass: WindowSizeClass,
    onBusinessSelected: (String) -> Unit
) {
    AdaptiveScaffold(
        windowSizeClass = windowSizeClass,
        topBar = {
            Text(
                "Pilih Bisnis",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        navigation = { /* No side nav here */ },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Belum ada bisnis yang terotorisasi untuk akun ini.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}

@Composable
private fun BusinessItem(name: String, onClick: () -> Unit) {
    NexovaCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, style = MaterialTheme.typography.titleMedium)
            Text("→", style = MaterialTheme.typography.titleMedium)
        }
    }
}
