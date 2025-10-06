package com.example.playlistmaker.player.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.service.AudioPlayerServiceController
import com.example.playlistmaker.player.ui.activity.AudioPlayerActivity

class AudioPlayerService : Service(), AudioPlayerServiceController {

    private val binder = AudioPlayerBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false
    private var completionListener: (() -> Unit)? = null

    private var trackName: String = ""
    private var artistName: String = ""

    inner class AudioPlayerBinder : Binder() {
        fun getService(): AudioPlayerServiceController = this@AudioPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "Service onBind")
        intent?.let {
            trackName = it.getStringExtra(EXTRA_TRACK_NAME) ?: ""
            artistName = it.getStringExtra(EXTRA_ARTIST_NAME) ?: ""
            val previewUrl = it.getStringExtra(EXTRA_PREVIEW_URL)
            Log.d(TAG, "Track: $trackName by $artistName, URL: $previewUrl")
            previewUrl?.let { url -> preparePlayer(url) }
        }
        return binder
    }

    override fun preparePlayer(url: String) {
        Log.d(TAG, "preparePlayer: $url")
        try {
            isPrepared = false
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.setOnPreparedListener {
                isPrepared = true
                Log.d(TAG, "MediaPlayer prepared")
            }
            mediaPlayer?.setOnErrorListener { _, what, extra ->
                isPrepared = false
                Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                true
            }
            mediaPlayer?.setOnCompletionListener {
                Log.d(TAG, "MediaPlayer completed")
                completionListener?.invoke()
            }
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            Log.e(TAG, "Error preparing player", e)
            isPrepared = false
        }
    }

    override fun play() {
        Log.d(TAG, "play() called, isPrepared=$isPrepared")
        if (!isPrepared) return
        try {
            mediaPlayer?.start()
            Log.d(TAG, "MediaPlayer started")
            showNotification()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error starting playback", e)
        }
    }

    override fun pause() {
        Log.d(TAG, "pause() called")
        if (!isPrepared) return
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                Log.d(TAG, "MediaPlayer paused")
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error pausing playback", e)
        }
    }

    override fun release() {
        Log.d(TAG, "release() called")
        try {
            mediaPlayer?.release()
            mediaPlayer = null
            isPrepared = false
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing player", e)
        }
    }

    override fun getCurrentPosition(): Int {
        return try {
            if (isPrepared) mediaPlayer?.currentPosition ?: 0 else 0
        } catch (e: IllegalStateException) {
            0
        }
    }

    override fun isPlaying(): Boolean {
        return try {
            isPrepared && (mediaPlayer?.isPlaying == true)
        } catch (e: IllegalStateException) {
            false
        }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        completionListener = listener
    }

    override fun showNotification() {
        Log.d(TAG, "showNotification()")
        try {
            val notification = createNotification()
            startForeground(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
        }
    }

    override fun hideNotification() {
        Log.d(TAG, "hideNotification()")
        try {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding notification", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Воспроизведение музыки",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Управление воспроизведением музыки"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(trackName)
            .setContentText(artistName)
            .setSmallIcon(R.drawable.search_icon)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy")
        super.onDestroy()
        release()
    }

    companion object {
        private const val TAG = "AudioPlayerService"
        const val EXTRA_PREVIEW_URL = "preview_url"
        const val EXTRA_TRACK_NAME = "track_name"
        const val EXTRA_ARTIST_NAME = "artist_name"

        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "audio_player_channel"
    }
}