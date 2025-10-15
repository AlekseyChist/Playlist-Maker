package com.example.playlistmaker.compose

import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.playlistmaker.ui.theme.customButtonColors

@Composable
fun AppBaseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
        colors = customButtonColors(),
        enabled = isEnabled
    ) { Text(text) }
}