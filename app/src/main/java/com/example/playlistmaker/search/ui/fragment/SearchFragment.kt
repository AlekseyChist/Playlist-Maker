package com.example.playlistmaker.search.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.Constants
import com.example.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.example.playlistmaker.search.ui.compose.SearchScreen
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // Получаем текущую тему
                val darkTheme = settingsViewModel.darkThemeEnabled.value ?: false

                SearchScreen(
                    viewModel = searchViewModel,
                    onTrackClick = { track ->
                        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
                        intent.putExtra(Constants.TRACK_KEY, track)
                        startActivity(intent)
                    },
                    darkTheme = darkTheme
                )
            }
        }
    }
}