package com.example.playlistmaker.media.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.usecase.PlaylistInteractor
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(
    protected val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    protected var playlistName: String = ""
    protected var playlistDescription: String? = null
    protected var playlistCoverUri: Uri? = null

    private val _playlistCreated = MutableLiveData<String>()
    val playlistCreated: LiveData<String> = _playlistCreated

    // Делаем open для возможности переопределения
    open fun onNameChanged(name: String) {
        playlistName = name
    }

    // Делаем open для возможности переопределения
    open fun onDescriptionChanged(description: String) {
        playlistDescription = description.ifBlank { null }
    }

    // Делаем open для возможности переопределения в EditPlaylistViewModel
    open fun onCoverSelected(uri: Uri) {
        playlistCoverUri = uri
    }

    open fun hasUnsavedChanges(): Boolean {
        return playlistName.isNotBlank() ||
                !playlistDescription.isNullOrBlank() ||
                playlistCoverUri != null
    }

    open fun createPlaylist() {
        if (playlistName.isBlank()) return

        viewModelScope.launch {
            try {
                val playlistId = playlistInteractor.createPlaylist(
                    name = playlistName,
                    description = playlistDescription,
                    coverUri = playlistCoverUri
                )
                _playlistCreated.value = playlistName
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}