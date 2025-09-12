package com.example.playlistmaker.media.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.usecase.PlaylistInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    playlistInteractor: PlaylistInteractor  // Убираем private
) : CreatePlaylistViewModel(playlistInteractor) {

    private var editingPlaylist: Playlist? = null
    private var originalCoverPath: String? = null
    private var hasNewCover = false

    private val _playlistData = MutableLiveData<Playlist>()
    val playlistData: LiveData<Playlist> = _playlistData

    private val _playlistUpdated = MutableLiveData<Boolean>()
    val playlistUpdated: LiveData<Boolean> = _playlistUpdated

    fun setEditingPlaylist(playlist: Playlist) {
        editingPlaylist = playlist
        originalCoverPath = playlist.coverPath
        _playlistData.value = playlist

        // Инициализируем поля родительского класса
        onNameChanged(playlist.name)
        playlist.description?.let { onDescriptionChanged(it) }
    }

    override fun onCoverSelected(uri: Uri) {
        super.onCoverSelected(uri)
        hasNewCover = true
    }

    fun updatePlaylist() {
        val playlist = editingPlaylist ?: return
        if (playlistName.isBlank()) return

        viewModelScope.launch {
            try {
                // Если выбрана новая обложка, сохраняем только её файл
                val finalCoverPath = if (hasNewCover && playlistCoverUri != null) {
                    playlistInteractor.saveCover(playlistCoverUri!!) // Нужно добавить этот метод
                } else {
                    originalCoverPath
                }

                val updatedPlaylist = playlist.copy(
                    name = playlistName,
                    description = playlistDescription,
                    coverPath = finalCoverPath
                )

                playlistInteractor.updatePlaylist(updatedPlaylist)
                _playlistUpdated.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun hasUnsavedChanges(): Boolean {
        val playlist = editingPlaylist ?: return false
        return playlistName != playlist.name ||
                playlistDescription != playlist.description ||
                hasNewCover
    }
}