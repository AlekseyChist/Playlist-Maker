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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.ui.viewmodel.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

open class CreatePlaylistFragment : Fragment() {

    protected var _binding: FragmentCreatePlaylistBinding? = null
    protected val binding get() = _binding!!

    protected open val viewModel: CreatePlaylistViewModel by viewModel()

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

    protected open fun setupViews() {
        binding.createButton.isEnabled = false
    }

    protected open fun setupListeners() {
        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onNameChanged(text?.toString() ?: "")
            binding.createButton.isEnabled = !text.isNullOrBlank()
        }

        binding.descriptionEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onDescriptionChanged(text?.toString() ?: "")
        }

        binding.coverImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.backButton.setOnClickListener {
            handleBackNavigation()
        }

        binding.createButton.setOnClickListener {
            viewModel.createPlaylist()
        }
    }

    protected open fun observeViewModel() {
        viewModel.playlistCreated.observe(viewLifecycleOwner) { playlistName ->
            showSuccessMessage(playlistName)
            findNavController().popBackStack()
        }
    }

    protected open fun setupBackNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackNavigation()
                }
            }
        )
    }

    protected open fun handleBackNavigation() {
        if (viewModel.hasUnsavedChanges()) {
            showExitConfirmationDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    protected open fun showExitConfirmationDialog() {
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

    protected fun displayCover(uri: Uri) {
        binding.addImageIcon.visibility = View.GONE
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8)))
            .into(binding.coverImageView)
    }

    protected fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    protected fun showSuccessMessage(playlistName: String) {
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