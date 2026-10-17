package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ComicDarkColorScheme = darkColorScheme(
    primary = ComicYellow,
    onPrimary = ComicBlack,
    primaryContainer = ComicOrange,
    onPrimaryContainer = ComicWhite,
    secondary = ComicCyan,
    onSecondary = ComicBlack,
    secondaryContainer = ComicDarkSurfaceVariant,
    onSecondaryContainer = ComicCyan,
    tertiary = ComicRed,
    onTertiary = ComicWhite,
    background = ComicDarkBg,
    onBackground = ComicTextPrimary,
    surface = ComicDarkSurface,
    onSurface = ComicTextPrimary,
    surfaceVariant = ComicDarkSurfaceVariant,
    onSurfaceVariant = ComicTextSecondary,
    outline = ComicCardBorder
)

private val ComicLightColorScheme = lightColorScheme(
    primary = ComicOrange,
    onPrimary = ComicWhite,
    primaryContainer = ComicYellow,
    onPrimaryContainer = ComicBlack,
    secondary = ComicPurple,
    onSecondary = ComicWhite,
    background = ComicWhite,
    onBackground = ComicBlack,
    surface = Color(0xFFF7F8FA),
    onSurface = ComicBlack,
    surfaceVariant = Color(0xFFE5E7EB),
    onSurfaceVariant = ComicDarkBg,
    outline = Color(0xFFD1D5DB)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek Comic Studio Dark Mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) ComicDarkColorScheme else ComicLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
