package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearch: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // ---- только цвета, без ломания верстки ----
    val isDark = isSystemInDarkTheme()
    val bgColor     = if (isDark) Color(0xFFFFFFFF) else Color(0xFFE6E8EB)
    val textColor   = Color(0xFF1A1B22)
    val hintIconCol = if (isDark) Color(0xFF1A1B22) else Color(0xFFAEAFB4)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Иконка поиска
            Icon(
                painter = painterResource(R.drawable.search_button),
                contentDescription = "Search",
                tint = hintIconCol,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Поле ввода
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = "Поиск",
                        style = MaterialTheme.typography.bodyLarge,
                        color = hintIconCol.copy(alpha = 1f)
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch(value) }),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Кнопка очистки
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = onClearClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.cancel_button),
                        contentDescription = "Clear",
                        tint = hintIconCol,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}