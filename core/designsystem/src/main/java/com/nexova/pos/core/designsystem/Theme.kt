package com.nexova.pos.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val Navy = Color(0xFF0B1026)
private val ElectricBlue = Color(0xFF1677FF)
private val Indigo = Color(0xFF4F46E5)
private val Purple = Color(0xFF7C3AED)
private val Orange = Color(0xFFFF9F1C)

private val LightColors = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    secondary = Indigo,
    tertiary = Purple,
    background = Color(0xFFF7F8FC),
    surface = Color.White,
    onBackground = Navy,
    onSurface = Navy,
    error = Color(0xFFBA1A1A)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8EB7FF),
    onPrimary = Color(0xFF002F67),
    secondary = Color(0xFFBEC2FF),
    tertiary = Color(0xFFD6BBFF),
    background = Navy,
    surface = Color(0xFF171A31),
    onBackground = Color(0xFFE4E3F2),
    onSurface = Color(0xFFE4E3F2),
    error = Color(0xFFFFB4AB)
)

@Composable
fun NexovaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

object NexovaBrand {
    val navy = Navy
    val electricBlue = ElectricBlue
    val indigo = Indigo
    val purple = Purple
    val orange = Orange
}

object NexovaSpacing {
    val xSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val xLarge = 32.dp
    val xxLarge = 48.dp
    val xxxLarge = 64.dp
}

private val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
)

private val Typography = androidx.compose.material3.Typography()
