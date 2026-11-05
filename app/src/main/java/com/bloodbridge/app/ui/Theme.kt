package com.bloodbridge.app.ui

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

val Brand = Color(0xFFE53945)
val BrandDark = Color(0xFFB91C2C)
val Ink = Color(0xFF101827)
val MutedRed = Color(0xFFFFE4E6)

private val Light = lightColorScheme(
    primary = Brand, onPrimary = Color.White,
    primaryContainer = MutedRed, onPrimaryContainer = BrandDark,
    secondary = Color(0xFF0F766E), onSecondary = Color.White,
    background = Color(0xFFF6F7FB), onBackground = Ink,
    surface = Color.White, onSurface = Ink,
    surfaceVariant = Color(0xFFF0F2F7), onSurfaceVariant = Color(0xFF647084),
    outline = Color(0xFFD9DEE8), error = Color(0xFFDC2626),
)
private val Dark = darkColorScheme(
    primary = Color(0xFFFF5964), onPrimary = Color(0xFF240609),
    primaryContainer = Color(0xFF4B1720), onPrimaryContainer = Color(0xFFFFD9DC),
    secondary = Color(0xFF5EEAD4), onSecondary = Color(0xFF062C29),
    background = Color(0xFF0B1018), onBackground = Color(0xFFF3F5F9),
    surface = Color(0xFF141C27), onSurface = Color(0xFFF3F5F9),
    surfaceVariant = Color(0xFF202B39), onSurfaceVariant = Color(0xFFADB8C8),
    outline = Color(0xFF344254), error = Color(0xFFFF737D),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(30.dp),
)

/** In-app theme choice: "system" | "light" | "dark" (persisted). */
object ThemePref {
    var mode by mutableStateOf("system")
        private set

    fun init(context: Context) {
        mode = context.getSharedPreferences("bloodbridge", Context.MODE_PRIVATE)
            .getString("theme", "system") ?: "system"
    }

    fun cycle(context: Context) {
        mode = when (mode) { "system" -> "light"; "light" -> "dark"; else -> "system" }
        context.getSharedPreferences("bloodbridge", Context.MODE_PRIVATE)
            .edit().putString("theme", mode).apply()
    }
}

@Composable
fun BloodBridgeTheme(content: @Composable () -> Unit) {
    val dark = when (ThemePref.mode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (dark) Dark else Light,
        typography = MaterialTheme.typography,
        shapes = AppShapes,
        content = content,
    )
}
