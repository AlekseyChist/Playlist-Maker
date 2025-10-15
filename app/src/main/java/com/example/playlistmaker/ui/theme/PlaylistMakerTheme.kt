package com.example.playlistmaker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// ---------- Общие цвета ----------
val ypPrimary = Color(0xFF3772E7)
val ypGray = Color(0xFFAEAFB4)
val ypLightGray = Color(0xFFE6E8EB)
val ypWhite = Color(0xFFFFFFFF)
val ypError = Color(0xFFF56B6C)

// ---------- Тёмная тема ----------
private val DarkColorScheme = darkColorScheme(
    primary = ypPrimary,
    onPrimary = Color.White,
    background = Color(0xFF1A1B22),
    onBackground = Color.White,
    surface = Color(0xFF1A1B22),
    onSurface = Color.White,
    surfaceVariant = ypGray,
    onSurfaceVariant = ypWhite,
    error = ypError
)

// ---------- Светлая тема ----------
private val LightColorScheme = lightColorScheme(
    primary = ypPrimary,
    onPrimary = Color.White,
    background = ypWhite,
    onBackground = Color.Black,
    surface = ypWhite,
    onSurface = Color.Black,
    surfaceVariant = ypGray,
    onSurfaceVariant = Color(0xFF1A1B22),
    error = ypError
)

// ---------- Основная функция темы ----------
@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Transparent,
        darkIcons = !darkTheme
    )

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}