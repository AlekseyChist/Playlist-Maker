package com.example.playlistmaker.media.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.ui.viewmodel.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { selectedUri ->
            viewModel.onCoverSelected(selectedUri)
            displayCover(selectedUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupListeners()
        observeViewModel()
        setupBackNavigation()
    }

    private fun setupViews() {
        // Начальное состояние кнопки "Создать"
        binding.createButton.isEnabled = false
    }

    private fun setupListeners() {
        // Слушатель для поля названия
        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onNameChanged(text?.toString() ?: "")
            binding.createButton.isEnabled = !text.isNullOrBlank()
        }

        // Слушатель для поля описания
        binding.descriptionEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onDescriptionChanged(text?.toString() ?: "")
        }

        // Клик по области обложки
        binding.coverImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Кнопка назад
        binding.backButton.setOnClickListener {
            handleBackNavigation()
        }

        // Кнопка создать
        binding.createButton.setOnClickListener {
            viewModel.createPlaylist()
        }
    }

    private fun observeViewModel() {
        viewModel.playlistCreated.observe(viewLifecycleOwner) { playlistName ->
            showSuccessMessage(playlistName)
            findNavController().popBackStack()
        }
    }

    private fun setupBackNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackNavigation()
                }
            }
        )
    }

    private fun handleBackNavigation() {
        if (viewModel.hasUnsavedChanges()) {
            showExitConfirmationDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    private fun displayCover(uri: Uri) {
        binding.coverImageView.setPadding(0, 0, 0, 0) // Убираем padding при загрузке изображения
        Glide.with(this)
            .load(uri)
            .transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius)))
            .into(binding.coverImageView)
    }

    private fun showSuccessMessage(playlistName: String) {
        Snackbar.make(
            binding.root,
            "Плейлист $playlistName создан",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}