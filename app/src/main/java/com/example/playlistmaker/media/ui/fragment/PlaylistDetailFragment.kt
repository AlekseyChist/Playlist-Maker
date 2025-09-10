package com.example.playlistmaker.media.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailBinding
import com.example.playlistmaker.media.ui.adapter.PlaylistTrackAdapter
import com.example.playlistmaker.media.ui.state.PlaylistDetailState
import com.example.playlistmaker.media.ui.viewmodel.PlaylistDetailViewModel
import com.example.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.sharing.domain.usecase.SharingInteractor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistDetailFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistDetailViewModel by viewModel()
    private val sharingInteractor: SharingInteractor by inject()

    private lateinit var playlistTrackAdapter: PlaylistTrackAdapter
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

        optionsBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Проверяем, что binding не null
                _binding?.let { binding ->
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                        }
                        else -> {
                            binding.overlay.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Проверяем, что binding не null
                _binding?.overlay?.alpha = (slideOffset + 1f) / 2f
            }
        })
    }

    private fun setupRecyclerView() {
        playlistTrackAdapter = PlaylistTrackAdapter(
            tracks = emptyList(),
            onItemClick = { track -> navigateToAudioPlayer(track) },
            onItemLongClick = { track -> showDeleteTrackDialog(track) }
        )

        binding.tracksRecyclerView.apply {
            adapter = playlistTrackAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Хотите удалить трек?")
            .setNegativeButton("НЕТ") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("ДА") { dialog, _ ->
                viewModel.removeTrackFromPlaylist(track)
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Хотите удалить плейлист?")
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Да") { dialog, _ ->
                viewModel.deletePlaylist()
                dialog.dismiss()
            }
            .show()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.shareButton.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.menuButton.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.overlay.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        // Опции в Bottom Sheet
        binding.optionsShareButton.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.sharePlaylist()
        }

        binding.editInfoButton.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            // Получаем текущий плейлист из состояния
            val currentState = viewModel.state.value
            if (currentState is PlaylistDetailState.Content) {
                val bundle = Bundle().apply {
                    putSerializable("playlist", currentState.playlist)
                }
                findNavController().navigate(
                    R.id.action_playlistDetailFragment_to_editPlaylistFragment,
                    bundle
                )
            }
        }

        binding.deletePlaylistButton.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
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

        viewModel.shareText.observe(viewLifecycleOwner) { shareText ->
            sharingInteractor.sharePlaylist(shareText)
        }

        viewModel.showEmptyPlaylistMessage.observe(viewLifecycleOwner) { shouldShow ->
            if (shouldShow) {
                Toast.makeText(
                    requireContext(),
                    "В этом плейлисте нет списка треков, которым можно поделиться",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModel.playlistDeleted.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                findNavController().popBackStack()
            }
        }
    }

    private fun sortTracksByAdditionOrder(tracks: List<Track>, trackIds: List<Long>): List<Track> {
        // Создаем мапу для быстрого доступа к трекам по ID
        val tracksMap = tracks.associateBy { it.trackId }

        // Сортируем по порядку в плейлисте, последние добавленные сверху
        return trackIds.reversed() // ← Реверсируем, чтобы последние были сверху
            .mapNotNull { trackId -> tracksMap[trackId] } // Получаем треки в нужном порядке
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

        // Продолжительность и количество треков
        val tracksCountText = viewModel.formatTracksCount(playlist.trackCount)
        binding.playlistInfo.text = "${content.totalDuration} · $tracksCountText"

        // Загружаем обложку
        loadPlaylistCover(playlist.coverPath)

        // Информация в options bottom sheet
        binding.optionsPlaylistName.text = playlist.name
        binding.optionsPlaylistSize.text = viewModel.formatTracksCount(playlist.trackCount)
        loadOptionsPlaylistCover(playlist.coverPath)


        val sortedTracks = sortTracksByAdditionOrder(content.tracks, playlist.trackIds)

        // Список треков (используем отсортированный список)
        playlistTrackAdapter.updateTracks(sortedTracks)

        // Показать/скрыть сообщение "нет треков"
        if (content.tracks.isEmpty()) {
            binding.noTracksText.visibility = View.VISIBLE
            tracksBottomSheetBehavior.isHideable = true
            tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            binding.noTracksText.visibility = View.GONE
            tracksBottomSheetBehavior.isHideable = false
            tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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

        // Очищаем callbacks перед уничтожением binding
        if (::optionsBottomSheetBehavior.isInitialized) {
            optionsBottomSheetBehavior.removeBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

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