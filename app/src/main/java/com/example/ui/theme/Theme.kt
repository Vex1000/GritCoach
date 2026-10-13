package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GritDarkColorScheme = darkColorScheme(
    primary = GritPrimaryAccent,
    onPrimary = GritBackground,
    primaryContainer = GritSecondaryAccent,
    onPrimaryContainer = GritSilver,
    secondary = GritSecondaryAccent,
    onSecondary = GritBackground,
    background = GritBackground,
    onBackground = GritTextPrimary,
    surface = GritCard,
    onSurface = GritTextPrimary,
    surfaceVariant = GritBackgroundSecondary,
    onSurfaceVariant = GritTextSecondary,
    outline = GritBorder,
    outlineVariant = GritBorder.copy(alpha = 0.5f)
)

@Composable
fun GritCoachTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GritDarkColorScheme,
        typography = Typography,
        content = content
    )
}

// Retain alias for compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    GritCoachTheme(content = content)
}
