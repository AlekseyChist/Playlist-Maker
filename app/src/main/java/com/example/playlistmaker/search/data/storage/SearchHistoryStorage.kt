package com.example.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.TrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryStorage(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    private val key = "search_history"
    private val maxHistorySize = 10

    fun addTrack(track: TrackDto) {
        val tracks = getTracks().toMutableList()
        tracks.removeAll { it.trackId == track.trackId }
        tracks.add(0, track)
        if (tracks.size > maxHistorySize) {
            tracks.removeAt(tracks.lastIndex)
        }
        saveTracks(tracks)
    }

    fun getTracks(): List<TrackDto> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<List<TrackDto>>() {}.type)
        } else {
            emptyList()
        }
    }

    private fun saveTracks(tracks: List<TrackDto>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(key).apply()
    }
}