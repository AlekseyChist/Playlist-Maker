package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository

interface SearchHistoryUseCase {
    suspend fun addTrack(track: Track) // Добавьте suspend
    suspend fun getHistory(): List<Track>
    suspend fun clearHistory()
}

class SearchHistoryUseCaseImpl(
    private val repository: TrackRepository
) : SearchHistoryUseCase {
    override suspend fun addTrack(track: Track) { // Добавьте suspend
        repository.addTrackToHistory(track)
    }

    override suspend fun getHistory(): List<Track> {
        return repository.getSearchHistory()
    }

    override suspend fun clearHistory() {
        repository.clearSearchHistory()
    }
}
