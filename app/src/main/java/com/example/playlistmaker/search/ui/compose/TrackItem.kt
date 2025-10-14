// app/src/main/java/com/example/playlistmaker/search/ui/compose/TrackItem.kt
package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import java.util.Locale

@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                indication = null, // ← ИСПРАВЛЕНИЕ: явно указываем null
                interactionSource = remember { MutableInteractionSource() } // ← ДОБАВЛЕНО
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Обложка трека
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = "Album cover",
            placeholder = painterResource(R.drawable.placeholder_image),
            error = painterResource(R.drawable.placeholder_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Информация о треке
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Название трека
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Артист и длительность
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Text(
                    text = " • ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = formatTrackTime(track.trackTimeMillis),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun formatTrackTime(trackTimeMillis: Long): String {
    val minutes = trackTimeMillis / 60000
    val seconds = (trackTimeMillis % 60000) / 1000
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}