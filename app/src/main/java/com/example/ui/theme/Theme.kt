package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ActionPrimaryDark,
    onPrimary = Color(0xFF1E2629),
    primaryContainer = Color(0xFF2C393E),
    onPrimaryContainer = Color(0xFFE8E0D9),
    secondary = AccentHighlight,
    onSecondary = Color(0xFF1E2629),
    tertiary = AccentAlternative,
    background = CanvasBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceElevatedDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    outlineVariant = BorderDark.copy(alpha = 0.5f),
    error = StatusDangerText,
    errorContainer = StatusDangerBg,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ActionPrimary,
    onPrimary = Color.White,
    primaryContainer = AccentHighlight.copy(alpha = 0.3f),
    onPrimaryContainer = ActionPrimary,
    secondary = AccentHighlight,
    onSecondary = ActionPrimary,
    tertiary = AccentAlternative,
    background = CanvasBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceElevatedLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    outlineVariant = BorderLight.copy(alpha = 0.7f),
    error = StatusDangerText,
    errorContainer = StatusDangerBg,
    onError = Color.White
)

@Composable
fun AuraEsteticaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
