package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandYellow,
    onPrimary = Color(0xFF090D16),
    primaryContainer = Color(0xFF715900),
    onPrimaryContainer = Color(0xFFFFE082),
    secondary = BrandCyan,
    onSecondary = Color(0xFF090D16),
    secondaryContainer = Color(0xFF004D5A),
    onSecondaryContainer = Color(0xFFA5F3FC),
    tertiary = BrandEmerald,
    onTertiary = Color(0xFF090D16),
    error = BrandRose,
    onError = Color.White,
    background = BrandDark,
    onBackground = TextPrimary,
    surface = BrandCard,
    onSurface = TextPrimary,
    surfaceVariant = BrandCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = BrandBorder
)

@Composable
fun CyclingFantasyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CyclingFantasyTheme(content = content)
}
