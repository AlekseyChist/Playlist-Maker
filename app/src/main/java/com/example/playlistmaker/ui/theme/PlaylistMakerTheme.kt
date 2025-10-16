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

/* ---------------- LIGHT THEME ---------------- */

@Composable
private fun lightScheme() = lightColorScheme(
    // Основные цвета
    primary            = Color(0xFF3772E7),       // синий
    onPrimary          = Color.White,

    // Контейнеры (тёмные кнопки на светлом фоне)
    primaryContainer   = Color(0xFF1A1B22),
    onPrimaryContainer = Color.White,

    // Фон приложения - БЕЛЫЙ
    background         = Color(0xFFFFFFFF),       // 🔥 БЕЛЫЙ ФОН
    onBackground       = Color(0xFF1A1B22),       // 🔥 ЧЁРНЫЙ ТЕКСТ

    // Поверхности (карточки, диалоги) - БЕЛЫЕ
    surface            = Color(0xFFFFFFFF),       // 🔥 БЕЛАЯ ПОВЕРХНОСТЬ
    onSurface          = Color(0xFF1A1B22),       // 🔥 ЧЁРНЫЙ ТЕКСТ

    // Варианты поверхностей
    surfaceVariant     = Color(0xFFE6E8EB),       // светло-серый фон полей
    onSurfaceVariant   = Color(0xFFAEAFB4),       // серый текст (вторичный)

    // Дополнительные
    outline            = Color(0xFFE6E8EB),       // разделители
    error              = Color(0xFFF56B6C)        // красный
)

/* ---------------- DARK THEME ---------------- */

@Composable
private fun darkScheme() = darkColorScheme(
    // Основные цвета
    primary            = Color(0xFF3772E7),       // синий
    onPrimary          = Color.White,

    // Контейнеры (белое поле на тёмном фоне)
    primaryContainer   = Color.White,
    onPrimaryContainer = Color(0xFF1A1B22),

    // Фон приложения - ТЁМНЫЙ
    background         = Color(0xFF1A1B22),       // 🔥 ТЁМНЫЙ ФОН
    onBackground       = Color.White,             // 🔥 БЕЛЫЙ ТЕКСТ

    // Поверхности (карточки, диалоги) - ТЁМНЫЕ
    surface            = Color(0xFF1A1B22),       // 🔥 ТЁМНАЯ ПОВЕРХНОСТЬ
    onSurface          = Color.White,             // 🔥 БЕЛЫЙ ТЕКСТ

    // Варианты поверхностей
    surfaceVariant     = Color(0xFF2D2E35),       // немного светлее фона
    onSurfaceVariant   = Color(0xFFAEAFB4),       // серый текст (вторичный)

    // Дополнительные
    outline            = Color(0xFF3D3E43),       // разделители
    error              = Color(0xFFE84749),       // красный
    onError            = Color.White
)

/* ---------------- MAIN THEME ---------------- */

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Настройка цвета статус-бара
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    val context = LocalContext.current
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        else -> {
            if (darkTheme) darkScheme()   // 🔥 Тёмная схема
            else lightScheme()            // 🔥 Светлая схема
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}

/* ---------------- HELPERS ---------------- */

@Composable
fun pmButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
)

/**
 * Цвета TextField:
 *  - Light: фон поля #E6E8EB, текст #1A1B22
 *  - Dark:  фон поля белый,    текст #1A1B22
 */
@Composable
fun pmTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        // Фон — используем primaryContainer (белый в дарке, серый в лайте)
        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,

        // Текст — используем onPrimaryContainer
        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),

        // Курсор и индикаторы
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )
}