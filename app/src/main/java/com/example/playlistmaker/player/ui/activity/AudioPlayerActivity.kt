package com.example.playlistmaker.player.ui.activity

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.adapter.PlaylistBottomSheetAdapter
import com.example.playlistmaker.media.ui.fragment.CreatePlaylistFragment
import com.example.playlistmaker.player.data.service.AudioPlayerService
import com.example.playlistmaker.player.domain.service.AudioPlayerServiceController
import com.example.playlistmaker.player.ui.state.AudioPlayerState
import com.example.playlistmaker.player.ui.view.PlaybackButtonView
import com.example.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlaylistAddStatus
import com.example.playlistmaker.search.domain.model.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private val viewModel: AudioPlayerViewModel by viewModel()

    private lateinit var track: Track
    private lateinit var backButton: ImageView
    private lateinit var albumCover: ImageView
    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var addToPlaylistButton: ImageView
    private lateinit var playButton: PlaybackButtonView
    private lateinit var likeButton: ImageView
    private lateinit var currentTimeTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var albumNameTextView: TextView
    private lateinit var yearTextView: TextView
    private lateinit var genreTextView: TextView
    private lateinit var countryTextView: TextView

    // Bottom Sheet элементы
    private lateinit var overlay: View
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var playlistsRecyclerView: RecyclerView
    private lateinit var newPlaylistButton: AppCompatButton
    private lateinit var playlistAdapter: PlaylistBottomSheetAdapter

    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    // Service connection
    private var serviceController: AudioPlayerServiceController? = null
    private var isBound = false

    // Проверка разрешений для уведомлений
    private var hasNotificationPermission = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected")
            val binder = service as? AudioPlayerService.AudioPlayerBinder
            serviceController = binder?.getService()
            isBound = true

            // Передаем сервис в ViewModel
            serviceController?.let { controller ->
                viewModel.setService(controller)
                Log.d(TAG, "Service set to ViewModel")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected")
            serviceController = null
            isBound = false
        }
    }

    // Launcher для запроса разрешения на уведомления
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasNotificationPermission = isGranted
        Log.d(TAG, "Notification permission granted: $isGranted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        Log.d(TAG, "onCreate")

        track = intent.getSerializableExtra(Constants.TRACK_KEY) as Track
        Log.d(TAG, "Track: ${track.trackName}")

        checkNotificationPermission()
        initViews()
        setupUI()
        setupListeners()
        setupBottomSheet()
        observeViewModel()

        // Передаем трек в ViewModel
        viewModel.setTrack(track)

        // Привязываем сервис
        bindAudioService()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasNotificationPermission) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            hasNotificationPermission = true
        }
    }

    private fun bindAudioService() {
        Log.d(TAG, "bindAudioService")
        val intent = Intent(this, AudioPlayerService::class.java).apply {
            putExtra(AudioPlayerService.EXTRA_PREVIEW_URL, track.previewUrl)
            putExtra(AudioPlayerService.EXTRA_TRACK_NAME, track.trackName)
            putExtra(AudioPlayerService.EXTRA_ARTIST_NAME, track.artistName)
        }

        try {
            val bound = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            Log.d(TAG, "bindService result: $bound")
        } catch (e: Exception) {
            Log.e(TAG, "Error binding service", e)
        }
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

        // Bottom Sheet views
        overlay = findViewById(R.id.overlay)
        playlistsRecyclerView = findViewById(R.id.playlistsRecyclerView)
        newPlaylistButton = findViewById(R.id.newPlaylistButton)
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

    private fun setupBottomSheet() {
        val bottomSheetContainer = findViewById<View>(R.id.playlistsBottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        playlistAdapter = PlaylistBottomSheetAdapter { playlist ->
            viewModel.addTrackToPlaylist(playlist)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        playlistsRecyclerView.apply {
            adapter = playlistAdapter
            layoutManager = LinearLayoutManager(this@AudioPlayerActivity)
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = (slideOffset + 1f) / 2f
            }
        })
    }

    private fun setupListeners() {
        backButton.setOnClickListener { finish() }

        playButton.setOnButtonClickListener {
            Log.d(TAG, "Play button clicked, state: ${viewModel.state.value}")
            when (viewModel.state.value) {
                is AudioPlayerState.Playing -> {
                    Log.d(TAG, "Calling pause()")
                    viewModel.pause()
                }
                is AudioPlayerState.Prepared,
                is AudioPlayerState.Paused -> {
                    Log.d(TAG, "Calling play()")
                    viewModel.play()
                }
                else -> {
                    Log.d(TAG, "State not ready for playback")
                }
            }
        }

        addToPlaylistButton.setOnClickListener {
            viewModel.loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        likeButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        newPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            supportFragmentManager.commit {
                add(android.R.id.content, CreatePlaylistFragment())
                addToBackStack(null)
            }
        }

        overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            Log.d(TAG, "State changed: $state")
            when (state) {
                is AudioPlayerState.Loading -> {
                    playButton.isEnabled = false
                }
                is AudioPlayerState.Prepared -> {
                    playButton.isEnabled = true
                    playButton.setState(PlaybackButtonView.ButtonState.PLAY)
                    currentTimeTextView.text = formatTime(0)
                }
                is AudioPlayerState.Playing -> {
                    playButton.setState(PlaybackButtonView.ButtonState.PAUSE)
                    currentTimeTextView.text = formatTime(state.currentPosition.toLong())
                }
                is AudioPlayerState.Paused -> {
                    playButton.setState(PlaybackButtonView.ButtonState.PLAY)
                }
                is AudioPlayerState.Error -> {
                    playButton.isEnabled = false
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.isFavorite.observe(this) { isFavorite ->
            updateFavoriteButton(isFavorite)
        }

        viewModel.playlists.observe(this) { playlists ->
            playlistAdapter.submitList(playlists)
        }

        viewModel.playlistAddStatus.observe(this) { status ->
            when (status) {
                is PlaylistAddStatus.Success -> {
                    Toast.makeText(
                        this,
                        "Добавлено в плейлист ${status.playlistName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is PlaylistAddStatus.AlreadyExists -> {
                    Toast.makeText(
                        this,
                        "Трек уже добавлен в плейлист ${status.playlistName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            likeButton.setImageResource(R.drawable.button_like_pressed)
        } else {
            likeButton.setImageResource(R.drawable.button_like_default)
        }
    }

    private fun formatTime(timeMillis: Long): String {
        return timeFormat.format(timeMillis)
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        if (hasNotificationPermission) {
            viewModel.onUiResumed()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        if (hasNotificationPermission) {
            viewModel.onUiPaused()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")

        // Останавливаем музыку только при закрытии Activity
        if (isFinishing) {
            viewModel.pause()
        }

        playButton.removeOnButtonClickListener()

        // Отвязываемся от сервиса
        if (isBound) {
            try {
                unbindService(serviceConnection)
                Log.d(TAG, "Service unbound")
            } catch (e: Exception) {
                Log.e(TAG, "Error unbinding service", e)
            }
            isBound = false
        }
    }

    companion object {
        private const val TAG = "AudioPlayerActivity"
    }
}