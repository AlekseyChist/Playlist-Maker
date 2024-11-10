package com.example.playlistmaker.presentation.ui.activities

import android.content.res.Resources
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var track: Track
    private lateinit var backButton: ImageView
    private lateinit var albumCover: ImageView
    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var addToPlaylistButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var likeButton: ImageView
    private lateinit var currentTimeTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var albumNameTextView: TextView
    private lateinit var yearTextView: TextView
    private lateinit var genreTextView: TextView
    private lateinit var countryTextView: TextView

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    var isPlaying = false
    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        track = intent.getSerializableExtra(Constants.TRACK_KEY) as Track

        initViews()
        setupUI()
        setupListeners()
        setupMediaPlayer()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        albumCover = findViewById(R.id.albumCover)
        trackNameTextView = findViewById(R.id.trackName)
        artistNameTextView = findViewById(R.id.artistName)
        addToPlaylistButton = findViewById(R.id.addToPlaylistButton)
        playButton = findViewById(R.id.playButton)
        likeButton = findViewById(R.id.likeButton)
        currentTimeTextView = findViewById(R.id.currentTime)
        durationTextView = findViewById(R.id.duration)
        albumNameTextView = findViewById(R.id.albumName)
        yearTextView = findViewById(R.id.year)
        genreTextView = findViewById(R.id.genre)
        countryTextView = findViewById(R.id.country)
    }

    private fun setupUI() {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        durationTextView.text = formatTime(track.trackTimeMillis)
        albumNameTextView.text = track.collectionName
        yearTextView.text = track.releaseDate.take(4)
        genreTextView.text = track.primaryGenreName
        countryTextView.text = track.country

        Glide.with(this)
            .load(track.artworkUrl100.replace("100x100", "512x512"))
            .placeholder(R.drawable.placeholder_image)
            .transform(RoundedCorners(dpToPx(8)))
            .into(albumCover)
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    private fun setupListeners() {
        backButton.setOnClickListener { finish() }

        playButton.setOnClickListener {
            if (isPlaying) pauseTrack() else playTrack()
        }

        addToPlaylistButton.setOnClickListener {
            // TODO: Implement add to playlist functionality
        }

        likeButton.setOnClickListener {
            // TODO: Implement like functionality
        }
    }

    private fun setupMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.previewUrl)
            prepareAsync()
            setOnPreparedListener { playButton.isEnabled = true }
            setOnCompletionListener {
                resetPlayerUI()
            }
        }
    }

    private fun playTrack() {
        mediaPlayer?.start()
        isPlaying = true
        playButton.setImageResource(R.drawable.pause_button)
        startTimer()
    }

    private fun pauseTrack() {
        mediaPlayer?.pause()
        isPlaying = false
        playButton.setImageResource(R.drawable.play_button)
        handler.removeCallbacksAndMessages(null)
    }

    private fun startTimer() {
        handler.post(object : Runnable {
            override fun run() {
                mediaPlayer?.currentPosition?.let { position ->
                    currentTimeTextView.text = formatTime(position.toLong())
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun formatTime(timeMillis: Long): String {
        return timeFormat.format(timeMillis)
    }

    private fun resetPlayerUI() {
        isPlaying = false
        playButton.setImageResource(R.drawable.play_button)
        currentTimeTextView.text = formatTime(0)
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
    }

    override fun onPause() {
        super.onPause()
        pauseTrack()
    }
}