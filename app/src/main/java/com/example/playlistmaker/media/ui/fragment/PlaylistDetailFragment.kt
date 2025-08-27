package com.example.playlistmaker.media.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailBinding
import com.example.playlistmaker.media.ui.state.PlaylistDetailState
import com.example.playlistmaker.media.ui.viewmodel.PlaylistDetailViewModel
import com.example.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistDetailFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistDetailViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var optionsBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheets()
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        // Получаем ID плейлиста из аргументов
        val playlistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: run {
            findNavController().popBackStack()
            return
        }

        viewModel.loadPlaylist(playlistId)
    }

    private fun setupBottomSheets() {
        // Bottom Sheet для треков
        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
        }

        // Bottom Sheet для опций
        optionsBottomSheetBehavior = BottomSheetBehavior.from(binding.optionsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
        }

        // Обработка состояний Bottom Sheet
        optionsBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset
            }
        })
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(emptyList()) { track ->
            navigateToAudioPlayer(track)
        }

        binding.tracksRecyclerView.apply {
            adapter = trackAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.shareButton.setOnClickListener {
            // TODO: Реализация поделиться
        }

        binding.menuButton.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // Клик по overlay
        binding.overlay.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        // Опции в Bottom Sheet
        binding.optionsShareButton.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            // TODO: Реализация поделиться
        }

        binding.editInfoButton.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            // TODO: Реализация редактирования
        }

        binding.deletePlaylistButton.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            // TODO: Реализация удаления
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistDetailState.Loading -> {
                    // Показать загрузку
                }
                is PlaylistDetailState.Content -> {
                    showContent(state)
                }
                is PlaylistDetailState.Error -> {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun showContent(content: PlaylistDetailState.Content) {
        val playlist = content.playlist

        // Основная информация
        binding.playlistName.text = playlist.name

        // Описание
        if (!playlist.description.isNullOrEmpty()) {
            binding.playlistDescription.text = playlist.description
            binding.playlistDescription.visibility = View.VISIBLE
        } else {
            binding.playlistDescription.visibility = View.GONE
        }

        // Продолжительность и количество треков (объединенная информация)
        val tracksCountText = viewModel.formatTracksCount(playlist.trackCount)
        binding.playlistInfo.text = "${content.totalDuration} · $tracksCountText"

        // Загружаем обложку
        loadPlaylistCover(playlist.coverPath)

        // Информация в options bottom sheet
        binding.optionsPlaylistName.text = playlist.name
        binding.optionsPlaylistSize.text = viewModel.formatTracksCount(playlist.trackCount)
        loadOptionsPlaylistCover(playlist.coverPath)

        // Список треков
        trackAdapter.updateTracks(content.tracks)

        // Показать/скрыть сообщение "нет треков"
        if (content.tracks.isEmpty()) {
            binding.noTracksText.visibility = View.VISIBLE
        } else {
            binding.noTracksText.visibility = View.GONE
        }
    }

    private fun loadPlaylistCover(coverPath: String?) {
        loadCoverToImageView(coverPath, binding.playlistCover)
    }

    private fun loadOptionsPlaylistCover(coverPath: String?) {
        loadCoverToImageView(coverPath, binding.optionsPlaylistCover)
    }

    private fun loadCoverToImageView(coverPath: String?, imageView: android.widget.ImageView) {
        if (!coverPath.isNullOrEmpty()) {
            val coverFile = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "playlist_covers/$coverPath"
            )

            if (coverFile.exists()) {
                Glide.with(this)
                    .load(coverFile)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .transform(CenterCrop(), RoundedCorners(8))
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.placeholder_image)
            }
        } else {
            imageView.setImageResource(R.drawable.placeholder_image)
        }
    }

    private fun navigateToAudioPlayer(track: Track) {
        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
        intent.putExtra(Constants.TRACK_KEY, track)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"

        fun newInstance(playlistId: Long): PlaylistDetailFragment {
            return PlaylistDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PLAYLIST_ID, playlistId)
                }
            }
        }
    }
}