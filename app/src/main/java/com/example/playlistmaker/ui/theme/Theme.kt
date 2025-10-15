package com.example.playlistmaker.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ---- tokens ----
val yp_primary = Color(0xFF3772E7)
val yp_gray = Color(0xFFAEAFB4)
val yp_bg_dark = Color(0xFF1A1B22)
val yp_bg_light = Color(0xFFFFFFFF)
val yp_surface_dark = Color(0xFF1A1B22)
val yp_surface_light = Color(0xFFFFFFFF)
val yp_primary_container_dark = Color(0xFFFFFFFF)
val yp_primary_container_light = Color(0xFF0A1B22)
val yp_onPrimary_container_dark = Color(0xFF1A1B22)
val yp_onPrimary_container_light = Color(0xFFECECEC)

private val darkScheme = darkColorScheme(
    primary = yp_primary,
    onPrimary = Color.White,
    background = yp_bg_dark,
    onBackground = Color.White,
    surface = yp_surface_dark,
    onSurface = Color.White,
    surfaceVariant = yp_gray,
    onSurfaceVariant = yp_bg_light,
    error = Color(0xFFF56B6C),
    primaryContainer = yp_primary_container_dark,
    onPrimaryContainer = yp_onPrimary_container_dark,
)

private val lightScheme = lightColorScheme(
    primary = yp_primary,
    onPrimary = Color.White,
    background = yp_bg_light,
    onBackground = Color.Black,
    surface = yp_surface_light,
    onSurface = Color.Black,
    surfaceVariant = yp_gray,
    onSurfaceVariant = yp_bg_dark,
    error = Color(0xFFF56B6C),
    primaryContainer = yp_primary_container_light,
    onPrimaryContainer = yp_onPrimary_container_light,
)

// хелперы под Material3
@Composable
fun customButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
)

@Composable
fun customEditTextFieldsColors(): TextFieldColors =
    TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedContainerColor = if (MaterialTheme.colorScheme == lightScheme) Color(0xFFE6E8EB) else Color.White,
        unfocusedContainerColor = if (MaterialTheme.colorScheme == lightScheme) Color(0xFFE6E8EB) else Color.White
    )