package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.SearchResponseDto
import com.example.playlistmaker.search.domain.model.Track
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class SearchResponse(
    val resultCount: Int,
    val results: List<Track>
)

interface iTunesApi {
    @GET("/search")
    fun search(
        @Query("term") term: String,
        @Query("entity") entity: String = "song"
    ): Call<SearchResponseDto>
}

object RetrofitClient {
    private const val BASE_URL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val iTunesApi = retrofit.create(com.example.playlistmaker.search.data.network.iTunesApi::class.java)

    fun searchTracks(term: String): Call<SearchResponseDto> {
        return iTunesApi.search(term)
    }
}