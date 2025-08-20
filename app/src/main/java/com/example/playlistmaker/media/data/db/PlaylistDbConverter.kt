package com.example.playlistmaker.media.data.db.converter

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter {

    private val gson = Gson()

    fun mapEntityToPlaylist(entity: PlaylistEntity): Playlist {
        val trackIds = if (entity.trackIds.isNotEmpty()) {
            gson.fromJson<List<Long>>(entity.trackIds, object : TypeToken<List<Long>>() {}.type)
        } else {
            emptyList()
        }

        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverPath = entity.coverPath,
            trackIds = trackIds,
            trackCount = entity.trackCount
        )
    }

    fun mapPlaylistToEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = gson.toJson(playlist.trackIds),
            trackCount = playlist.trackCount
        )
    }
}