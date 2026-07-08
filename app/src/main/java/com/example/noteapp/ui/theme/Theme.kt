package com.example.noteapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AuroraDarkScheme = darkColorScheme(
    primary = AuroraTeal,
    secondary = AuroraViolet,
    tertiary = AuroraBlue,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkField,
    outline = DarkCardBorder,
    outlineVariant = DarkChipBorder,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextMuted
)

private val AuroraLightScheme = lightColorScheme(
    primary = AuroraTeal,
    secondary = AuroraViolet,
    tertiary = AuroraBlue,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightField,
    outline = LightCardBorder,
    outlineVariant = LightChipBorder,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextMuted
)

@Composable
fun NoteAppTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) AuroraDarkScheme else AuroraLightScheme,
        typography = Typography,
        content = content
    )


}

