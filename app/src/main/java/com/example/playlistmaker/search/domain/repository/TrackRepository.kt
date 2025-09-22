package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query

interface TrackRepository {
    fun searchTracks(query: String): Flow<List<Track>>
    suspend fun addTrackToHistory(track: Track) // Добавьте suspend
    suspend fun getSearchHistory(): List<Track>
    suspend fun clearSearchHistory()
}