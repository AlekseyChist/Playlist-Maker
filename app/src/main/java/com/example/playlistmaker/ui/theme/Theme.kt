package com.example.playlistmaker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Цвета из вашего приложения
private val Blue = Color(0xFF3772E7)
private val YpBlack = Color(0xFF1A1B22)
private val YpWhite = Color(0xFFFFFFFF)
private val YpTextGrey = Color(0xFFAEAFB4)
private val YpLightGray = Color(0xFFE6E8EB)

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = YpWhite,
    secondary = Blue,
    onSecondary = YpBlack,
    background = YpWhite,
    onBackground = YpBlack,
    surface = YpWhite,
    onSurface = YpBlack,
    outline = YpTextGrey
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue,
    onPrimary = YpWhite,
    secondary = Blue,
    onSecondary = YpWhite,
    background = YpBlack,
    onBackground = YpWhite,
    surface = YpBlack,
    onSurface = YpWhite,
    outline = YpTextGrey
)

@Composable
fun PlaylistMakerTheme(
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