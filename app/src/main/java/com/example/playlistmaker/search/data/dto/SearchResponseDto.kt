package com.example.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName

data class SearchResponseDto(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<TrackDto>
)