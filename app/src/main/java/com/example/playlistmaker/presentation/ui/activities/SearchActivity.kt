package com.example.playlistmaker.presentation.ui.activities

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
import com.example.playlistmaker.di.Constants
import com.example.playlistmaker.di.Creator
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.state.SearchState
import com.example.playlistmaker.presentation.ui.adapters.TrackAdapter
import com.example.playlistmaker.presentation.viewmodels.SearchViewModel


class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var refreshButton: Button
    private lateinit var adapter: TrackAdapter
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyTitle: TextView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar

    private val searchViewModel: SearchViewModel by lazy {
        SearchViewModel(
            Creator.provideSearchTracksUseCase(),
            Creator.provideSearchHistoryUseCase()
        )
    }

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val debounceHandler = Handler(Looper.getMainLooper())
    private var debouncedClick: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupListeners()
        setupRecyclerViews()
        setupObservers()
    }

    private fun setupObservers() {
        searchViewModel.state.observe(this) { state ->
            when(state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showResults(state.tracks)
                is SearchState.Empty -> showNoResults()
                is SearchState.Error -> showError()
                is SearchState.History -> showHistory(state.tracks)
            }
        }
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
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            } else {
                false
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s?.isNotEmpty() == true) View.VISIBLE else View.GONE
                if (s?.isNotEmpty() == true) {
                    hideAllContent()
                    debounceSearch()
                } else {
                    updateHistoryVisibility()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            // Обновляем видимость истории при изменении фокуса
            if (hasFocus && searchEditText.text.isEmpty()) {
                updateHistoryVisibility()
            } else {
                historyLayout.visibility = View.GONE
            }
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            clearButton.visibility = View.GONE
            hideKeyboard()
            updateHistoryVisibility()
        }

        backButton.setOnClickListener {
            finish()
        }

        refreshButton.setOnClickListener {
            performSearch()
        }

        clearHistoryButton.setOnClickListener {
            searchViewModel.clearHistory()
            updateHistoryVisibility()
        }
    }

    private fun setupRecyclerViews() {
        adapter = TrackAdapter(emptyList()) { track ->
            debouncedTrackClick(track)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        historyAdapter = TrackAdapter(emptyList()) { track ->
            debouncedTrackClick(track)
        }
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun updateHistoryVisibility() {
        val historyTracks = searchViewModel.getHistory()
        // Показываем историю только если:
        // 1. Поле поиска пустое
        // 2. История не пустая
        // 3. Поле поиска в фокусе
        val showHistory = searchEditText.text.isEmpty() &&
                historyTracks.isNotEmpty() &&
                searchEditText.hasFocus()

        if (showHistory) {
            historyAdapter.updateTracks(historyTracks)
            historyLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            placeholderLayout.visibility = View.GONE
        } else {
            historyLayout.visibility = View.GONE
            recyclerView.visibility = if (adapter.itemCount > 0) View.VISIBLE else View.GONE
            placeholderLayout.visibility = View.GONE
        }
    }

    private fun debouncedTrackClick(track: Track) {
        debouncedClick?.let { debounceHandler.removeCallbacks(it) }
        debouncedClick = Runnable {
            searchViewModel.addToHistory(track)
            navigateToAudioPlayer(track)
        }
        debounceHandler.postDelayed(debouncedClick!!, 300)
    }

    private fun debounceSearch() {
        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = Runnable {
            performSearch()
        }
        handler.postDelayed(searchRunnable!!, 2000)
    }

    private fun performSearch() {
        val query = searchEditText.text.toString().trim()
        searchViewModel.search(query)
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
    }

    private fun showResults(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        placeholderLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        adapter.updateTracks(tracks)
    }

    private fun showNoResults() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.nothing_found)
        placeholderText.text = getString(R.string.nothing_found)
        refreshButton.visibility = View.GONE
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

    private fun showHistory(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.GONE
        if (tracks.isNotEmpty() && searchEditText.hasFocus()) {
            historyLayout.visibility = View.VISIBLE
            historyAdapter.updateTracks(tracks)
        } else {
            historyLayout.visibility = View.GONE
        }
    }

    private fun hideAllContent() {
        historyLayout.visibility = View.GONE
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.GONE
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

    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { handler.removeCallbacks(it) }
        debouncedClick?.let { debounceHandler.removeCallbacks(it) }
    }
}