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
import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.data.network.SearchResponse
import com.example.playlistmaker.di.App
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.adapters.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

    private val searchTracksUseCase by lazy {
        (application as App).getSearchTracksUseCase()
    }

    private val searchHistoryUseCase by lazy {
        (application as App).getSearchHistoryUseCase()
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
            searchHistoryUseCase.clearHistory()
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

    private fun debouncedTrackClick(track: Track) {
        debouncedClick?.let { debounceHandler.removeCallbacks(it) }
        debouncedClick = Runnable {
            searchHistoryUseCase.addTrack(track)
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
        if (query.isNotEmpty()) {
            showLoading()
            Thread {
                try {
                    val tracks = searchTracksUseCase.execute(query)
                    runOnUiThread {
                        hideLoading()
                        when {
                            tracks.isEmpty() -> showNoResults()
                            else -> showResults(tracks)
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        hideLoading()
                        showError()
                    }
                }
            }.start()
        } else {
            updateHistoryVisibility()
        }
    }

    private fun showLoading() {
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun showNoResults() {
        hideLoading()
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.nothing_found)
        placeholderText.text = getString(R.string.nothing_found)
        refreshButton.visibility = View.GONE
    }

    private fun showResults(tracks: List<Track>) {
        hideLoading()
        recyclerView.visibility = View.VISIBLE
        placeholderLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        adapter.updateTracks(tracks)
    }

    private fun showError() {
        hideLoading()
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.connection_error)
        placeholderText.text = getString(R.string.connection_error)
        refreshButton.visibility = View.VISIBLE
    }

    private fun updateHistoryVisibility() {
        val historyTracks = searchHistoryUseCase.getHistory()
        val showHistory = searchEditText.text.isEmpty() && historyTracks.isNotEmpty()

        historyLayout.visibility = if (showHistory) View.VISIBLE else View.GONE
        recyclerView.visibility = if (!showHistory && adapter.itemCount > 0) View.VISIBLE else View.GONE
        placeholderLayout.visibility = View.GONE

        if (showHistory) {
            historyAdapter.updateTracks(historyTracks)
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
        intent.putExtra("track", track)
        startActivity(intent)
    }
}