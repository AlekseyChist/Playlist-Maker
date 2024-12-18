package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository

interface SearchHistoryUseCase {
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}

class SearchHistoryUseCaseImpl(
    private val repository: TrackRepository
) : SearchHistoryUseCase {
    override fun addTrack(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun getHistory(): List<Track> {
        return repository.getSearchHistory()
    }

    override fun clearHistory() {
        repository.clearSearchHistory()
    }
}
