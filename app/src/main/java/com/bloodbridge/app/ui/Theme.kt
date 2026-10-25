package com.bloodbridge.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Brand = Color(0xFFDC2626)
val BrandDark = Color(0xFF991B1B)

private val Light = lightColorScheme(
    primary = Brand, onPrimary = Color.White,
    primaryContainer = Color(0xFFFEE2E2), onPrimaryContainer = BrandDark,
    background = Color(0xFFFAFAFA), surface = Color.White,
)
private val Dark = darkColorScheme(
    primary = Color(0xFFEF4444), onPrimary = Color.White,
    primaryContainer = Color(0xFF450A0A), onPrimaryContainer = Color(0xFFFECACA),
    background = Color(0xFF0F1117), surface = Color(0xFF181B23),
)

@Composable
fun BloodBridgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
