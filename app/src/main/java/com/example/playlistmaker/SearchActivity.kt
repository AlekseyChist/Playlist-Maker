package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

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
    private lateinit var searchHistory: SearchHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupListeners()
        setupRecyclerViews()

        searchHistory = SearchHistory(getSharedPreferences("search_history", MODE_PRIVATE))
        updateHistoryVisibility()
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

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            clearButton.visibility = View.GONE
            hideKeyboard()
            updateHistoryVisibility()
        }

        backButton.setOnClickListener {
            finish()
        }

        searchEditText.addTextChangedListener { text ->
            clearButton.visibility = if (text?.isNotEmpty() == true) View.VISIBLE else View.GONE
            if (text?.isNotEmpty() == true) {
                hideAllContent()
            } else {
                updateHistoryVisibility()
            }
        }

        refreshButton.setOnClickListener {
            performSearch()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            updateHistoryVisibility()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                updateHistoryVisibility()
            }
        }
    }

    private fun setupRecyclerViews() {
        adapter = TrackAdapter(emptyList()) { track ->
            searchHistory.addTrack(track)
            openAudioPlayer(track)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        historyAdapter = TrackAdapter(emptyList()) { track ->
            searchHistory.addTrack(track)
            updateHistoryVisibility()
            openAudioPlayer(track)
        }
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun openAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra("track", track)
        }
        startActivity(intent)
    }

    private fun updateHistoryVisibility() {
        val historyTracks = searchHistory.getTracks()
        val showHistory = searchEditText.text.isEmpty() && historyTracks.isNotEmpty()

        historyLayout.isVisible = showHistory
        recyclerView.isVisible = !showHistory && adapter.itemCount > 0
        placeholderLayout.isVisible = false

        if (showHistory) {
            historyAdapter.updateTracks(historyTracks)
        }
    }

    private fun performSearch() {
        val query = searchEditText.text.toString().trim()
        if (query.isNotEmpty()) {
            showLoading()
            lifecycleScope.launch {
                try {
                    val response = iTunesApiService.searchTracks(query)
                    if (response.results.isEmpty()) {
                        showNoResults()
                    } else {
                        showResults(response.results)
                    }
                } catch (e: Exception) {
                    showError()
                }
            }
        } else {
            updateHistoryVisibility()
        }
    }

    private fun showLoading() {
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        // Здесь можно добавить отображение индикатора загрузки
    }

    private fun showNoResults() {
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.nothing_found)
        placeholderText.text = getString(R.string.nothing_found)
        refreshButton.visibility = View.GONE
    }

    private fun showResults(tracks: List<Track>) {
        recyclerView.visibility = View.VISIBLE
        placeholderLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        adapter.updateTracks(tracks)
    }

    private fun showError() {
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.connection_error)
        placeholderText.text = getString(R.string.connection_error)
        refreshButton.visibility = View.VISIBLE
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
}