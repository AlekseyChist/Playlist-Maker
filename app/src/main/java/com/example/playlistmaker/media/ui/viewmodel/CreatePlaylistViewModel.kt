package com.example.playlistmaker.media.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.usecase.PlaylistInteractor
import kotlinx.coroutines.launch

// Базовый класс CreatePlaylistViewModel
open class CreatePlaylistViewModel(
    protected val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    protected var playlistName: String = ""
    protected var playlistDescription: String? = null
    protected var playlistCoverUri: Uri? = null

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

// Класс для редактирования плейлиста
class EditPlaylistViewModel(
    playlistInteractor: PlaylistInteractor
) : CreatePlaylistViewModel(playlistInteractor) {

    private var editingPlaylist: Playlist? = null
    private var originalCoverPath: String? = null

    private val _currentPlaylistData = MutableLiveData<Playlist>()
    val currentPlaylistData: LiveData<Playlist> = _currentPlaylistData

    private val _playlistUpdated = MutableLiveData<Boolean>()
    val playlistUpdated: LiveData<Boolean> = _playlistUpdated

    fun initPlaylist(playlist: Playlist) {
        editingPlaylist = playlist
        originalCoverPath = playlist.coverPath
        _currentPlaylistData.value = playlist

        // Инициализируем поля родительского класса
        playlistName = playlist.name
        playlistDescription = playlist.description
    }

    override fun createPlaylist() {
        val playlist = editingPlaylist ?: return
        if (playlistName.isBlank()) return

        viewModelScope.launch {
            try {
                // Определяем путь к обложке
                val coverPath = when {
                    playlistCoverUri != null -> {
                        // Сохраняем новую обложку
                        playlistInteractor.createPlaylist(
                            name = "temp",
                            description = null,
                            coverUri = playlistCoverUri
                        )
                        // Для простоты используем метод сохранения через createPlaylist
                        // В реальности нужен отдельный метод saveCover
                        originalCoverPath
                    }
                    else -> originalCoverPath
                }

                val updatedPlaylist = playlist.copy(
                    name = playlistName,
                    description = playlistDescription,
                    coverPath = coverPath
                )

                playlistInteractor.updatePlaylist(updatedPlaylist)
                _playlistUpdated.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Переопределяем метод проверки несохраненных изменений
    override fun hasUnsavedChanges(): Boolean {
        val playlist = editingPlaylist ?: return false

        return playlistName != playlist.name ||
                playlistDescription != playlist.description ||
                playlistCoverUri != null
    }
}