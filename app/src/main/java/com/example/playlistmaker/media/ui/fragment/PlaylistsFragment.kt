// app/src/main/java/com/example/playlistmaker/media/ui/fragment/PlaylistsFragment.kt
package com.example.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.ui.adapter.PlaylistAdapter
import com.example.playlistmaker.media.ui.state.PlaylistsState
import com.example.playlistmaker.media.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter { playlist ->
            // TODO: Навигация на экран плейлиста
        }

        binding.playlistsRecyclerView.apply {
            adapter = playlistAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupListeners() {
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsState.Empty -> showEmptyState()
                is PlaylistsState.Content -> showContent(state.playlists)
            }
        }
    }

    private fun showEmptyState() {
        binding.playlistsRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
    }

    private fun showContent(playlists: List<com.example.playlistmaker.media.domain.model.Playlist>) {
        binding.playlistsRecyclerView.visibility = View.VISIBLE
        binding.emptyStateLayout.visibility = View.GONE
        playlistAdapter.submitList(playlists)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}