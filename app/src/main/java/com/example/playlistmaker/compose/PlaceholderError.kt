package com.example.playlistmaker.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R

@Composable
fun PlaceholderError(error: Errors) {
    val (text, imageRes) = when (error) {
        Errors.NoFavourites      -> "Ваша медиатека пуста" to R.drawable.nothing_found
        Errors.NoPlaylists       -> "Вы не создали ни одного плейлиста" to R.drawable.nothing_found
        Errors.SearchNoConnection-> "Нет подключения к интернету" to R.drawable.connection_error
        Errors.SearchNothingFound-> "По вашему запросу ничего не найдено" to R.drawable.nothing_found
        Errors.NoTracksInPlaylist-> "В плейлисте отсутствуют треки" to R.drawable.nothing_found
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(imageRes), contentDescription = null)
        Spacer(Modifier.height(16.dp))
        Text(text)
    }
}