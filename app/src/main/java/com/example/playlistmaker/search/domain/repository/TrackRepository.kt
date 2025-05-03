package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query

interface TrackRepository {
    fun searchTracks(query: String): Flow<List<Track>>
    fun addTrackToHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}