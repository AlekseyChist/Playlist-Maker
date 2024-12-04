package com.example.playlistmaker.domain.usecases.tracks

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repositories.TrackRepository

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
