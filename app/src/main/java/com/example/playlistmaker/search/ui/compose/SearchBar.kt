package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AppSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = stringResource(R.string.search),
    // если хочешь, можешь подставлять свои иконки/экшены:
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingActions: @Composable RowScope.() -> Unit = {},
    // управляем активностью извне ИЛИ локально через упрощённую версию ниже
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
) {
    val kb = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    androidx.compose.material3.SearchBar(
        modifier = modifier
            .widthIn(min = 0.dp, max = 720.dp) // чтобы на планшетах не растягивалось на всю ширину
            .focusRequester(focusRequester),
        query = query,
        onQueryChange = onQueryChange,
        onSearch = {
            kb?.hide()
            onSearch()
            onActiveChange(false)
        },
        active = active,
        onActiveChange = onActiveChange,
        placeholder = { Text(placeholderText) },
        leadingIcon = leadingIcon ?: {
            Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    onClear()
                    onQueryChange("")
                    onActiveChange(true)
                    kb?.show()
                }) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "Clear")
                }
            }
        },
        // нейтральные цвета, можно заменить на свои из темы
        colors = SearchBarDefaults.colors()
    ) {
        // Контент выпадашки (подсказки/история) — если нужен:
        // SuggestionsContent()
    }

    // автофокус когда строка активируется
    LaunchedEffect(active) { if (active) focusRequester.requestFocus() }
}