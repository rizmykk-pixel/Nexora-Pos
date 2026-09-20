package com.nexova.pos.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nexova.pos.core.domain.WindowSizeClass

@Composable
fun AdaptiveScaffold(
    windowSizeClass: WindowSizeClass,
    navigation: @Composable (isExpanded: Boolean) -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val isExpanded = windowSizeClass != WindowSizeClass.COMPACT
    
    Scaffold(
        topBar = topBar,
        bottomBar = { if (!isExpanded) bottomBar() },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Row(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isExpanded) {
                navigation(true)
            }
            content(PaddingValues())
        }
    }
}
