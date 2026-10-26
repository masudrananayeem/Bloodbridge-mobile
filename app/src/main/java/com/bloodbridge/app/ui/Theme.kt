package com.bloodbridge.app.ui

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

/** In-app theme choice: "system" | "light" | "dark" (persisted). */
object ThemePref {
    var mode by mutableStateOf("system")
        private set

    fun init(context: Context) {
        mode = context.getSharedPreferences("bloodbridge", Context.MODE_PRIVATE).getString("theme", "system") ?: "system"
    }

    fun cycle(context: Context) {
        mode = when (mode) { "system" -> "light"; "light" -> "dark"; else -> "system" }
        context.getSharedPreferences("bloodbridge", Context.MODE_PRIVATE).edit().putString("theme", mode).apply()
    }
}

@Composable
fun BloodBridgeTheme(content: @Composable () -> Unit) {
    val dark = when (ThemePref.mode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}
