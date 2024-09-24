package com.example.playlistmaker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var albumCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var albumName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var duration: TextView
    private lateinit var currentTime: TextView
    private lateinit var playButton: ImageView
    private lateinit var addToPlaylistButton: ImageView
    private lateinit var likeButton: ImageView

    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())
    private var currentPlaybackPosition = 0L
    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        initViews()

        track = intent.getSerializableExtra("track") as? Track
        track?.let { displayTrackInfo(it) }

        setupClickListeners()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        albumCover = findViewById(R.id.albumCover)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        albumName = findViewById(R.id.albumName)
        genre = findViewById(R.id.genre)
        country = findViewById(R.id.country)
        duration = findViewById(R.id.duration)
        currentTime = findViewById(R.id.currentTime)
        playButton = findViewById(R.id.playButton)
        addToPlaylistButton = findViewById(R.id.addToPlaylistButton)
        likeButton = findViewById(R.id.likeButton)
        releaseDate = findViewById(R.id.year)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }
        playButton.setOnClickListener { togglePlayback() }
        addToPlaylistButton.setOnClickListener { addToPlaylist() }
        likeButton.setOnClickListener { toggleLike() }
    }

    private fun displayTrackInfo(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName


        albumName.text = track.collectionName ?:"Unknown"
        releaseDate.text = extractYear(track.releaseDate)


        genre.text = track.primaryGenreName
        country.text = track.country
        duration.text = formatDuration(track.trackTimeMillis)
        currentTime.text = formatDuration(0)

        loadAlbumCover(track.artworkUrl100)
    }

    private fun loadAlbumCover(artworkUrl: String) {
        val highQualityArtworkUrl = artworkUrl.replace("100x100bb.jpg", "512x512bb.jpg")
        Glide.with(this)
            .load(highQualityArtworkUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius)))
            .into(albumCover)
    }

    private fun togglePlayback() {
        isPlaying = !isPlaying
        if (isPlaying) {
            playButton.setImageResource(R.drawable.pause_button)
            startPlaybackSimulation()
        } else {
            playButton.setImageResource(R.drawable.play_button)
            stopPlaybackSimulation()
        }
    }

    private fun startPlaybackSimulation() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isPlaying) {
                    currentPlaybackPosition += 1000 // Увеличиваем на 1 секунду
                    updateCurrentTime(currentPlaybackPosition)
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000)
    }

    private fun stopPlaybackSimulation() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun updateCurrentTime(position: Long) {
        currentTime.text = formatDuration(position)
    }

    private fun addToPlaylist() {
        // TODO: Implement adding to playlist functionality
    }

    private fun toggleLike() {
        // TODO: Implement like/unlike functionality
    }

    private fun extractYear(releaseDate: String): String {
        Log.d("AudioPlayerActivity", "Release date: $releaseDate")
        return try {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(releaseDate)
            val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
            Log.d("AudioPlayerActivity", "Extracted year: $year")
            year
        } catch (e: Exception) {
            Log.e("AudioPlayerActivity", "Error parsing release date: ${e.message}")
            "Unknown"
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) -
                TimeUnit.MINUTES.toSeconds(minutes)
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlaybackSimulation()
    }
}