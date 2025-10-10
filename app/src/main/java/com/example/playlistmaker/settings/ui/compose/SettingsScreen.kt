package com.example.playlistmaker.settings.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val darkThemeEnabled by viewModel.darkThemeEnabled.observeAsState(false)

    PlaylistMakerTheme(darkTheme = darkThemeEnabled) {
        Scaffold(
            topBar = {
                SettingsTopBar(onBackClick = onBackClick)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Переключатель темной темы
                SettingsItem(
                    title = stringResource(R.string.Dark_theme),
                    icon = null,
                    onClick = null
                ) {
                    Switch(
                        checked = darkThemeEnabled,
                        onCheckedChange = { viewModel.switchTheme(it) }
                    )
                }

                HorizontalDivider()

                // Поделиться приложением
                SettingsItem(
                    title = stringResource(R.string.Share_the_app),
                    icon = R.drawable.share_button,
                    onClick = { viewModel.shareApp() }
                )

                HorizontalDivider()

                // Написать в поддержку
                SettingsItem(
                    title = stringResource(R.string.write_to_the_support),
                    icon = R.drawable.support_logo,
                    onClick = {
                        viewModel.writeToSupport(
                            email = "alexeychistyakoм@yandex.ru",
                            subject = "Сообщение разработчикам",
                            body = "Спасибо за крутое приложение!"
                        )
                    }
                )

                HorizontalDivider()

                // Пользовательское соглашение
                SettingsItem(
                    title = stringResource(R.string.User_agreement),
                    icon = R.drawable.user_agreement,
                    onClick = {
                        viewModel.openUserAgreement("https://yandex.ru/legal/practicum_offer/")
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.Settings),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.back_button_vector),
                    contentDescription = "Назад"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun SettingsItem(
    title: String,
    icon: Int?,
    onClick: (() -> Unit)?,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = onClick,
                        indication = null, // Убираем ripple, чтобы избежать конфликта
                        interactionSource = remember { MutableInteractionSource() }
                    )
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 16.dp, vertical = 21.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        if (trailingContent != null) {
            trailingContent()
        } else if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}