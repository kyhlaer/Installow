package com.installow.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.installow.app.model.LanguagePreference
import com.installow.app.model.ThemePreference
import com.installow.app.ui.i18n.LocalAppStrings
import com.installow.app.ui.i18n.resolveAppLanguage
import com.installow.app.ui.i18n.stringsFor

@Immutable
data class InstallowPalette(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceVariant: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val accentContrastBg: Color,
    val accentContrastFg: Color
)

private val DarkPalette = InstallowPalette(
    isDark = true,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceElevated = DarkSurfaceElevated,
    surfaceVariant = DarkSurfaceVariant,
    border = DarkBorder,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textMuted = DarkTextMuted,
    accentContrastBg = Color(0xFFF4F4F6),
    accentContrastFg = Color(0xFF0A0A0D)
)

private val LightPalette = InstallowPalette(
    isDark = false,
    background = LightBackground,
    surface = LightSurface,
    surfaceElevated = LightSurfaceElevated,
    surfaceVariant = LightSurfaceVariant,
    border = LightBorder,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textMuted = LightTextMuted,
    accentContrastBg = Color(0xFF111115),
    accentContrastFg = Color(0xFFFFFFFF)
)

val LocalInstallowPalette = staticCompositionLocalOf { DarkPalette }

private val DarkColorScheme = darkColorScheme(
    primary = BrandRose,
    onPrimary = Color.White,
    secondary = AccentIndigo,
    onSecondary = Color.White,
    tertiary = AccentBlue,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = AccentCoral
)

private val LightColorScheme = lightColorScheme(
    primary = BrandRose,
    onPrimary = Color.White,
    secondary = AccentIndigo,
    onSecondary = Color.White,
    tertiary = AccentBlue,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = AccentCoral
)

@Composable
fun InstallowTheme(
    themePreference: ThemePreference = ThemePreference.DARK,
    languagePreference: LanguagePreference = LanguagePreference.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDark = when (themePreference) {
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
    }
    val palette = if (useDark) DarkPalette else LightPalette
    val colorScheme = if (useDark) DarkColorScheme else LightColorScheme
    val appStrings = stringsFor(resolveAppLanguage(languagePreference))

    CompositionLocalProvider(
        LocalInstallowPalette provides palette,
        LocalAppStrings provides appStrings
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = InstallowTypography,
            content = content
        )
    }
}
