package com.example.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.viewmodel.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel: EditPlaylistViewModel by viewModel()

    private var editingPlaylist: Playlist? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем данные плейлиста из аргументов
        editingPlaylist = arguments?.getSerializable(ARG_PLAYLIST) as? Playlist

        editingPlaylist?.let { playlist ->
            setupEditMode(playlist)
            viewModel.setEditingPlaylist(playlist)
        }

        observeEditViewModel()
    }

    private fun setupEditMode(playlist: Playlist) {
        // Находим TextView в toolbar и меняем текст
        val toolbarTitle = binding.toolbar.findViewById<TextView>(R.id.toolbarTitle)
        toolbarTitle?.text = "Редактировать"

        // Меняем текст кнопки
        binding.createButton.text = "Сохранить"

        // Заполняем поля
        binding.nameEditText.setText(playlist.name)
        playlist.description?.let {
            binding.descriptionEditText.setText(it)
        }

        // Загружаем обложку, если она есть
        playlist.coverPath?.let { coverPath ->
            val coverFile = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "playlist_covers/$coverPath"
            )

            if (coverFile.exists()) {
                binding.addImageIcon.visibility = View.GONE
                Glide.with(this)
                    .load(coverFile)
                    .centerCrop()
                    .transform(RoundedCorners(dpToPx(8)))
                    .into(binding.coverImageView)
            }
        }
    }

    override fun setupListeners() {
        super.setupListeners()

        // Переопределяем обработчик кнопки создания/сохранения
        binding.createButton.setOnClickListener {
            viewModel.updatePlaylist()
        }
    }

    override fun handleBackNavigation() {
        // При редактировании выходим без подтверждения
        findNavController().popBackStack()
    }

    private fun observeEditViewModel() {
        viewModel.playlistUpdated.observe(viewLifecycleOwner) { isUpdated ->
            if (isUpdated) {
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        private const val ARG_PLAYLIST = "playlist"
    }
}