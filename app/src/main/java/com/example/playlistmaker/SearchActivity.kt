package com.example.playlistmaker

import android.content.Context
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupListeners()
        setupRecyclerView()
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
            showEmptyState()
        }

        backButton.setOnClickListener {
            finish()
        }

        searchEditText.addTextChangedListener {
            clearButton.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        refreshButton.setOnClickListener {
            performSearch()
        }
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun performSearch() {
        val query = searchEditText.text.toString().trim()
        if (query.isNotEmpty()) {
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
            showEmptyState()
        }
    }

    private fun showNoResults() {
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.VISIBLE
        placeholderImage.setImageResource(R.drawable.nothing_found)
        placeholderText.text = getString(R.string.nothing_found)
        refreshButton.visibility = View.GONE
    }

    private fun showResults(tracks: List<Track>) {
        recyclerView.visibility = View.VISIBLE
        placeholderLayout.visibility = View.GONE
        adapter.updateTracks(tracks)
    }

    private fun showError() {
        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.VISIBLE
        placeholderImage.setImageResource(R.drawable.connection_error)
        placeholderText.text = getString(R.string.connection_error)
        refreshButton.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
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