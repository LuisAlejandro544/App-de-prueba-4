package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CleanDarkPrimary,
    onPrimary = CleanDarkBackground,
    primaryContainer = CleanDarkPrimaryContainer,
    onPrimaryContainer = CleanDarkText,
    secondary = CleanDarkSecondary,
    onSecondary = CleanDarkBackground,
    background = CleanDarkBackground,
    surface = CleanDarkSurface,
    onBackground = CleanDarkText,
    onSurface = CleanDarkText,
    surfaceVariant = CleanDarkSurfaceVariant,
    onSurfaceVariant = CleanDarkText,
    outline = CleanDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = CleanPrimary,
    onPrimary = Color.White,
    primaryContainer = CleanPrimaryContainer,
    onPrimaryContainer = CleanPrimary,
    secondary = CleanSecondary,
    onSecondary = Color.White,
    background = CleanBackground,
    surface = CleanSurface,
    onBackground = CleanText,
    onSurface = CleanText,
    surfaceVariant = CleanSurfaceVariant,
    onSurfaceVariant = CleanSecondary,
    outline = CleanOutline
)

@Composable
fun MyApplicationTheme(
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
