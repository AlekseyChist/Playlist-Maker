package com.example.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.TrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.edit

class SearchHistoryStorage(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    private val key = "search_history"
    private val maxHistorySize = 10

    suspend fun addTrack(track: TrackDto) = withContext(Dispatchers.IO) {
        val tracks = getTracks().toMutableList()
        tracks.removeAll { it.trackId == track.trackId }
        tracks.add(0, track)
        if (tracks.size > maxHistorySize) {
            tracks.removeAt(tracks.lastIndex)
        }
        saveTracks(tracks)
    }

    suspend fun getTracks(): List<TrackDto> = withContext(Dispatchers.IO) {
        val json = sharedPreferences.getString(key, null)
        if (json != null) {
            gson.fromJson(json, object : TypeToken<List<TrackDto>>() {}.type)
        } else {
            emptyList()
        }
    }

    private suspend fun saveTracks(tracks: List<TrackDto>) = withContext(Dispatchers.IO) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit { putString(key, json) }
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        sharedPreferences.edit { remove(key) }
    }
}