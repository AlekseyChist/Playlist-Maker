package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.listener.TracksConsumer
import com.example.playlistmaker.search.domain.model.Track

interface TrackRepository {
    fun searchTracks(query: String, consumer: TracksConsumer)
    fun addTrackToHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}