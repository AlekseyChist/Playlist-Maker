package com.example.playlistmaker.settings.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val darkThemeEnabled by viewModel.darkThemeEnabled.collectAsState()

    PlaylistMakerTheme(darkTheme = darkThemeEnabled) {
        Scaffold(
            topBar = { SettingsTopBar(onBackClick) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SettingsItem(
                    title = stringResource(R.string.Dark_theme),
                    icon = null,
                    onClick = null
                ) {
                    Switch(
                        checked = darkThemeEnabled,
                        onCheckedChange = { viewModel.switchTheme(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF3772E7), // Синий ползунок
                            checkedTrackColor = Color(0xFF3772E7).copy(alpha = 0.5f), // Полупрозрачный трек
                            uncheckedThumbColor = Color(0xFFAEAFB4), // Серый ползунок
                            uncheckedTrackColor = Color(0xFFAEAFB4).copy(alpha = 0.5f), // Полупрозрачный трек
                            uncheckedBorderColor = Color.Transparent,
                            checkedBorderColor = Color.Transparent
                        )
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                SettingsItem(
                    title = stringResource(R.string.Share_the_app),
                    icon = R.drawable.share_button,
                    onClick = { viewModel.shareApp() }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                SettingsItem(
                    title = stringResource(R.string.write_to_the_support),
                    icon = R.drawable.support_logo,
                    onClick = {
                        viewModel.writeToSupport(
                            email = "support@example.com",
                            subject = "Поддержка",
                            body = "Здравствуйте!"
                        )
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                SettingsItem(
                    title = stringResource(R.string.User_agreement),
                    icon = R.drawable.user_agreement,
                    onClick = {
                        viewModel.openUserAgreement("https://example.com/agreement")
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
        title = { Text(text = stringResource(R.string.Settings), style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.back_button_vector),
                    contentDescription = "Back"
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
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null)
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        when {
            trailing != null -> trailing()
            icon != null -> Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}