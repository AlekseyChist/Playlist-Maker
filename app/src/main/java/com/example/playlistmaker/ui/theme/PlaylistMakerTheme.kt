package com.example.playlistmaker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/* -------- LIGHT -------- */

@Composable
private fun lightScheme() = lightColorScheme(
    primary            = Color(0xFF3772E7),
    onPrimary          = Color.White,

    // тёмные кнопки на светлом фоне (пилюля «Очистить историю»)
    primaryContainer   = Color(0xFF1A1B22),
    onPrimaryContainer = Color.White,

    // фон/поверхности
    background         = Color(0xFFFFFFFF),
    onBackground       = Color(0xFF1A1B22),
    surface            = Color(0xFFFFFFFF),
    onSurface          = Color(0xFF1A1B22),

    // вторичные
    surfaceVariant     = Color(0xFFE6E8EB), // карточки/поля
    onSurfaceVariant   = Color(0xFFAEAFB4), // серый текст/иконки

    outline            = Color(0xFFE6E8EB),
    error              = Color(0xFFF56B6C)
)

/* -------- DARK -------- */

@Composable
private fun darkScheme() = darkColorScheme(
    primary            = Color(0xFF3772E7),
    onPrimary          = Color.White,

    // белые контейнеры на тёмном фоне (поле поиска белое)
    primaryContainer   = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF1A1B22),

    background         = Color(0xFF1A1B22),
    onBackground       = Color(0xFFFFFFFF),

    surface            = Color(0xFF1A1B22),
    onSurface          = Color(0xFFFFFFFF),

    surfaceVariant     = Color(0xFF2D2E35),
    onSurfaceVariant   = Color(0xFFAEAFB4),

    outline            = Color(0xFF3D3E43),
    error              = Color(0xFFE84749),
    onError            = Color.White
)

/* -------- THEME -------- */

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    val context = LocalContext.current
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        else -> if (darkTheme) darkScheme() else lightScheme()
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}

/* -------- HELPERS -------- */

@Composable
fun pmButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
)

/**
 * Цвета TextField, именно для «поле поиска» из ТЗ:
 *  - Light: фон #E6E8EB, плейсхолдер/иконки #AEAFB4, текст #1A1B22
 *  - Dark:  фон #FFFFFF, плейсхолдер/иконки #1A1B22, текст #1A1B22
 */
@Composable
fun pmSearchFieldColors(): TextFieldColors {
    val isDark = isSystemInDarkTheme()
    val container = if (isDark) Color(0xFFFFFFFF) else Color(0xFFE6E8EB)
    val textColor = Color(0xFF1A1B22)
    val hintOrIcon = if (isDark) Color(0xFF1A1B22) else Color(0xFFAEAFB4)

    return TextFieldDefaults.colors(
        focusedContainerColor = container,
        unfocusedContainerColor = container,
        disabledContainerColor = container,

        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = textColor.copy(alpha = 0.5f),

        cursorColor = MaterialTheme.colorScheme.primary,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,

        // Эти два используются для Icon/placeholder, но мы всё равно красим их явно в экране
        focusedLeadingIconColor = hintOrIcon,
        unfocusedLeadingIconColor = hintOrIcon,
        focusedTrailingIconColor = hintOrIcon,
        unfocusedTrailingIconColor = hintOrIcon,
        focusedPlaceholderColor = hintOrIcon,
        unfocusedPlaceholderColor = hintOrIcon
    )
}