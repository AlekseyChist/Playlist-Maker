package com.example.playlistmaker.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.Constants
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.example.playlistmaker.search.ui.state.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupListeners()
        setupRecyclerViews()
        observeViewModel()
    }

    private fun initViews() {
        binding.clearButton.visibility = View.GONE
    }

    private fun setupListeners() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearButton.visibility = if (s?.isNotEmpty() == true) View.VISIBLE else View.GONE
                viewModel.search(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(binding.searchEditText.text.toString())
                true
            } else {
                false
            }
        }

        binding.clearButton.setOnClickListener {
            binding.searchEditText.text.clear()
            binding.clearButton.visibility = View.GONE
            hideKeyboard()
            viewModel.showHistory()
        }

        binding.refreshButton.setOnClickListener {
            viewModel.search(binding.searchEditText.text.toString())
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) {
                updateHistoryVisibility()
            }
        }
    }

    private fun setupRecyclerViews() {
        trackAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.addToHistory(track)
            navigateToAudioPlayer(track)
        }
        binding.recyclerView.adapter = trackAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        historyAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.addToHistory(track)
            navigateToAudioPlayer(track)
        }
        binding.historyRecyclerView.adapter = historyAdapter
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showContent(state.tracks)
                is SearchState.Error -> showError()
                is SearchState.Empty -> showEmpty()
                is SearchState.History -> showHistory(state.tracks)
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.placeholderLayout.visibility = View.GONE
        binding.historyLayout.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.placeholderLayout.visibility = View.GONE
        binding.historyLayout.visibility = View.GONE
        trackAdapter.updateTracks(tracks)
    }

    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.placeholderLayout.visibility = View.VISIBLE
        binding.historyLayout.visibility = View.GONE
        binding.placeholderImage.setImageResource(R.drawable.connection_error)
        binding.placeholderText.text = getString(R.string.connection_error)
        binding.refreshButton.visibility = View.VISIBLE
    }

    private fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.placeholderLayout.visibility = View.VISIBLE
        binding.historyLayout.visibility = View.GONE
        binding.placeholderImage.setImageResource(R.drawable.nothing_found)
        binding.placeholderText.text = getString(R.string.nothing_found)
        binding.refreshButton.visibility = View.GONE
    }

    private fun showHistory(tracks: List<Track>) {
        historyAdapter.updateTracks(tracks)
        binding.historyLayout.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.placeholderLayout.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun updateHistoryVisibility() {
        val historyTracks = viewModel.getHistory()
        val showHistory = binding.searchEditText.text.isEmpty() &&
                historyTracks.isNotEmpty() &&
                binding.searchEditText.hasFocus()

        if (showHistory) {
            viewModel.showHistory()
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun navigateToAudioPlayer(track: Track) {
        // Пока используем Intent, позже заменим на Navigation
        val intent = android.content.Intent(requireContext(), com.example.playlistmaker.player.ui.activity.AudioPlayerActivity::class.java)
        intent.putExtra(Constants.TRACK_KEY, track)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}