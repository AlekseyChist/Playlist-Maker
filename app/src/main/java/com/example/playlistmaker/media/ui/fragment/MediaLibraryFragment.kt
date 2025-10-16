package com.example.playlistmaker.media.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState  // 🆕
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.compose.MediaLibraryScreen
import com.example.playlistmaker.media.ui.viewmodel.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {

    private val favoriteTracksViewModel: FavoriteTracksViewModel by viewModel()
    private val playlistsViewModel: PlaylistsViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val darkTheme by settingsViewModel.darkThemeEnabled.collectAsState()

                MediaLibraryScreen(
                    favoriteTracksViewModel = favoriteTracksViewModel,
                    playlistsViewModel = playlistsViewModel,
                    onTrackClick = { track ->
                        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
                        intent.putExtra(Constants.TRACK_KEY, track)
                        startActivity(intent)
                    },
                    onPlaylistClick = { playlist ->
                        val bundle = Bundle().apply {
                            putLong("playlist_id", playlist.id)
                        }
                        findNavController().navigate(
                            R.id.action_mediaLibraryFragment_to_playlistDetailFragment,
                            bundle
                        )
                    },
                    onCreatePlaylistClick = {
                        findNavController().navigate(
                            R.id.action_mediaLibraryFragment_to_createPlaylistFragment
                        )
                    },
                    darkTheme = darkTheme
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        playlistsViewModel.loadPlaylists()
    }
}