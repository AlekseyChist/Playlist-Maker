package com.example.playlistmaker.media.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistDetailFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistDetailViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter

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

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        // Получаем ID плейлиста из аргументов
        val playlistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: run {
            // Если нет аргументов, возвращаемся назад
            findNavController().popBackStack()
            return
        }

        viewModel.loadPlaylist(playlistId)
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
            // TODO: Реализация поделиться (будет позже)
            android.util.Log.d("PlaylistDetail", "Share button clicked")
        }

        binding.menuButton.setOnClickListener {
            // TODO: Реализация меню (будет позже)
            android.util.Log.d("PlaylistDetail", "Menu button clicked")
        }
    }

    private fun navigateToAudioPlayer(track: Track) {
        // Навигация к аудиоплееру через Intent (пока Activity)
        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
        intent.putExtra(Constants.TRACK_KEY, track)
        startActivity(intent)
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistDetailState.Loading -> {
                    showLoading()
                }
                is PlaylistDetailState.Content -> {
                    hideLoading()
                    showContent(state)
                }
                is PlaylistDetailState.Error -> {
                    hideLoading()
                    showError()
                }
            }
        }
    }

    private fun showLoading() {
        // Можно показать прогресс бар
        // Пока просто скрываем контент
        binding.playlistName.visibility = View.GONE
        binding.playlistInfo.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.playlistName.visibility = View.VISIBLE
        binding.playlistInfo.visibility = View.VISIBLE
    }

    private fun showError() {
        // Показываем ошибку и возвращаемся назад
        findNavController().popBackStack()
    }

    private fun showContent(content: PlaylistDetailState.Content) {
        val playlist = content.playlist

        // Заполняем UI данными плейлиста
        binding.playlistName.text = playlist.name

        // Показываем/скрываем описание
        if (!playlist.description.isNullOrEmpty()) {
            binding.playlistDescription.text = playlist.description
            binding.playlistDescription.visibility = View.VISIBLE
        } else {
            binding.playlistDescription.visibility = View.GONE
        }

        // Информация о продолжительности и количестве треков
        val tracksCountText = viewModel.formatTracksCount(playlist.trackCount)
        binding.playlistInfo.text = "${content.totalDuration} • $tracksCountText"

        // Загружаем обложку
        loadPlaylistCover(playlist.coverPath)

        // Обновляем список треков
        trackAdapter.updateTracks(content.tracks)

        // Показываем/скрываем заголовок треков
        if (content.tracks.isNotEmpty()) {
            binding.tracksTitle.visibility = View.VISIBLE
            binding.tracksRecyclerView.visibility = View.VISIBLE
        } else {
            binding.tracksTitle.visibility = View.GONE
            binding.tracksRecyclerView.visibility = View.GONE
        }
    }

    private fun loadPlaylistCover(coverPath: String?) {
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
                    .transform(CenterCrop(), RoundedCorners(dpToPx(8)))
                    .into(binding.playlistCover)
            } else {
                // Файл не найден, показываем заглушку
                binding.playlistCover.setImageResource(R.drawable.placeholder_image)
            }
        } else {
            // Нет пути к обложке, показываем заглушку
            binding.playlistCover.setImageResource(R.drawable.placeholder_image)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"

        /**
         * Создает новый экземпляр фрагмента с переданным ID плейлиста
         */
        fun newInstance(playlistId: Long): PlaylistDetailFragment {
            return PlaylistDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PLAYLIST_ID, playlistId)
                }
            }
        }
    }
}