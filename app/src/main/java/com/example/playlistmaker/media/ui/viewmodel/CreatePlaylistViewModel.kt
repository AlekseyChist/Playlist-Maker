package com.example.playlistmaker.media.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.usecase.PlaylistInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var playlistName: String = ""
    private var playlistDescription: String? = null
    private var playlistCoverUri: Uri? = null

    private val _playlistCreated = MutableLiveData<String>()
    val playlistCreated: LiveData<String> = _playlistCreated

    fun onNameChanged(name: String) {
        playlistName = name
    }

    fun onDescriptionChanged(description: String) {
        playlistDescription = description.ifBlank { null }
    }

    fun onCoverSelected(uri: Uri) {
        playlistCoverUri = uri
    }

    fun hasUnsavedChanges(): Boolean {
        return playlistName.isNotBlank() ||
                !playlistDescription.isNullOrBlank() ||
                playlistCoverUri != null
    }

    fun createPlaylist() {
        if (playlistName.isBlank()) return

        viewModelScope.launch {
            try {
                playlistInteractor.createPlaylist(
                    name = playlistName,
                    description = playlistDescription,
                    coverUri = playlistCoverUri
                )
                _playlistCreated.value = playlistName
            } catch (e: Exception) {
                // Обработка ошибки
                e.printStackTrace()
            }
        }
    }
}