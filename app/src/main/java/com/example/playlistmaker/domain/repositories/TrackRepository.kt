package com.example.playlistmaker.domain.repositories

import com.example.playlistmaker.domain.models.Track

interface TrackRepository {
    fun searchTracks(query: String): List<Track>
    fun addTrackToHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}