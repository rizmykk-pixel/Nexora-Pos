package com.nexova.pos.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class BadgeStatus {
    SUCCESS, WARNING, ERROR, INFO, NEUTRAL
}

@Composable
fun NexovaStatusBadge(
    text: String,
    status: BadgeStatus,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        BadgeStatus.SUCCESS -> Color(0xFFE6F4EA)
        BadgeStatus.WARNING -> Color(0xFFFFF4E5)
        BadgeStatus.ERROR -> Color(0xFFFCE8E8)
        BadgeStatus.INFO -> Color(0xFFE8F0FE)
        BadgeStatus.NEUTRAL -> Color(0xFFF1F3F4)
    }
    
    val contentColor = when (status) {
        BadgeStatus.SUCCESS -> Color(0xFF137333)
        BadgeStatus.WARNING -> Color(0xFFB06000)
        BadgeStatus.ERROR -> Color(0xFFC5221F)
        BadgeStatus.INFO -> Color(0xFF1967D2)
        BadgeStatus.NEUTRAL -> Color(0xFF3C4043)
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}
