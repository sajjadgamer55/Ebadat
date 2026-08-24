package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = Emerald400,
    onPrimary = Emerald900,
    primaryContainer = Emerald700,
    onPrimaryContainer = Emerald100,
    secondary = GoldBase,
    onSecondary = Emerald900,
    secondaryContainer = Emerald800,
    onSecondaryContainer = GoldLight,
    tertiary = GoldMuted,
    background = NightBackground,
    onBackground = TextLightPrimary,
    surface = NightSurface,
    onSurface = TextLightPrimary,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = TextLightSecondary,
    outline = NightCardBorder,
    outlineVariant = Emerald800
)

private val LightColorScheme = lightColorScheme(
    primary = Emerald700,
    onPrimary = Color.White,
    primaryContainer = Emerald100,
    onPrimaryContainer = Emerald900,
    secondary = GoldDark,
    onSecondary = Color.White,
    secondaryContainer = GoldLight,
    onSecondaryContainer = Emerald900,
    tertiary = Emerald600,
    background = CreamBackground,
    onBackground = TextDarkPrimary,
    surface = CreamSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = TextDarkSecondary,
    outline = CreamCardBorder,
    outlineVariant = GoldLight
)

@Composable
fun WirdiTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        // Force RTL layout direction across the entire application for authentic Arabic typography & UX
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            content()
        }
    }
}
