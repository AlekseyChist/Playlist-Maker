package com.example.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.Constants
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.example.playlistmaker.search.ui.state.SearchState
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel


class SearchActivity : AppCompatActivity() {
    private val searchViewModel: SearchViewModel by lazy {
        Creator.provideSearchViewModel()
    }

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var refreshButton: Button
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyTitle: TextView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupListeners()
        setupRecyclerViews()
        observeViewModel()
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        backButton = findViewById(R.id.button_back)
        recyclerView = findViewById(R.id.recyclerView)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderText = findViewById(R.id.placeholderText)
        refreshButton = findViewById(R.id.refreshButton)
        placeholderLayout = findViewById(R.id.placeholderLayout)
        historyLayout = findViewById(R.id.historyLayout)
        historyTitle = findViewById(R.id.historyTitle)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)

        clearButton.visibility = View.GONE
    }

    private fun setupListeners() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s?.isNotEmpty() == true) View.VISIBLE else View.GONE
                searchViewModel.search(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchViewModel.search(searchEditText.text.toString())
                true
            } else {
                false
            }
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            clearButton.visibility = View.GONE
            hideKeyboard()
            searchViewModel.showHistory()
        }

        backButton.setOnClickListener {
            finish()
        }

        refreshButton.setOnClickListener {
            searchViewModel.search(searchEditText.text.toString())
        }

        clearHistoryButton.setOnClickListener {
            searchViewModel.clearHistory()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                updateHistoryVisibility()
            }
        }
    }

    private fun setupRecyclerViews() {
        trackAdapter = TrackAdapter(emptyList()) { track ->
            searchViewModel.addToHistory(track)
            navigateToAudioPlayer(track)
        }
        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        historyAdapter = TrackAdapter(emptyList()) { track ->
            searchViewModel.addToHistory(track)
            navigateToAudioPlayer(track)
        }
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        searchViewModel.state.observe(this) { state ->
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
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        placeholderLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        trackAdapter.updateTracks(tracks)
    }

    private fun showError() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.connection_error)
        placeholderText.text = getString(R.string.connection_error)
        refreshButton.visibility = View.VISIBLE
    }

    private fun showEmpty() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.nothing_found)
        placeholderText.text = getString(R.string.nothing_found)
        refreshButton.visibility = View.GONE
    }

    private fun showHistory(tracks: List<Track>) {
        historyAdapter.updateTracks(tracks)
        historyLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun updateHistoryVisibility() {
        val historyTracks = searchViewModel.getHistory()
        val showHistory = searchEditText.text.isEmpty() &&
                historyTracks.isNotEmpty() &&
                searchEditText.hasFocus()

        if (showHistory) {
            searchViewModel.showHistory()
        }
    }

    private fun hideKeyboard() {
        currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun navigateToAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(Constants.TRACK_KEY, track)
        startActivity(intent)
    }
}