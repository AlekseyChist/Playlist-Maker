package com.example.playlistmaker.media.domain.usecase

import android.net.Uri

interface CoverStorage {
    suspend fun saveCover(uri: Uri): String
    fun getCoverPath(fileName: String): String
}