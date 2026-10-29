package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// Dynamic Font Size Composition Locals
val LocalArabicFontSize = compositionLocalOf<TextUnit> { 28.sp }
val LocalUrduFontSize = compositionLocalOf<TextUnit> { 17.sp }

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = EmeraldDarkBackground,
    primaryContainer = GoldContainer,
    onPrimaryContainer = GoldOnContainer,
    secondary = IslamicGreenAccent,
    onSecondary = EmeraldDarkBackground,
    secondaryContainer = EmeraldDarkContainer,
    onSecondaryContainer = TextWhite,
    tertiary = WarmSand,
    onTertiary = EmeraldDarkBackground,
    background = EmeraldDarkBackground,
    onBackground = TextWhite,
    surface = EmeraldDarkSurface,
    onSurface = TextWhite,
    surfaceVariant = EmeraldDarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = EmeraldBorder,
    outlineVariant = EmeraldDarkContainer
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimaryLight,
    onPrimary = ParchmentLightSurface,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = EmeraldOnContainerLight,
    secondary = GoldDark,
    onSecondary = ParchmentLightSurface,
    secondaryContainer = GoldOnContainer,
    onSecondaryContainer = GoldContainer,
    tertiary = WarmSand,
    onTertiary = TextDarkPrimary,
    background = ParchmentLightBackground,
    onBackground = TextDarkPrimary,
    surface = ParchmentLightSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = ParchmentLightSurfaceVariant,
    onSurfaceVariant = TextDarkMuted,
    outline = GoldPrimary,
    outlineVariant = ParchmentLightSurfaceVariant
)

@Composable
fun NoorTheme(
    darkTheme: Boolean = true, // Default to rich Emerald Dark theme
    arabicFontSize: TextUnit = 28.sp,
    urduFontSize: TextUnit = 17.sp,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalArabicFontSize provides arabicFontSize,
        LocalUrduFontSize provides urduFontSize
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
