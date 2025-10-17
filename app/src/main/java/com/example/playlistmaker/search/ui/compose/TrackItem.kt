// app/src/main/java/com/example/playlistmaker/search/ui/compose/TrackItem.kt
package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import com.example.playlistmaker.compose.formatTrackTimeMillis
import com.example.playlistmaker.search.domain.model.Track

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
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
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
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp))
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_tracks_divider),
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 6.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = formatTrackTimeMillis(track.trackTimeMillis),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // ИСПРАВЛЕНО: Добавлен tint для стрелки
        Icon(
            painter = painterResource(R.drawable.user_agreement),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(20.dp),
            // Используем outline - в светлой теме будет #AEAFB4, в темной #FFFFFF
            tint = MaterialTheme.colorScheme.outline
        )
    }
}