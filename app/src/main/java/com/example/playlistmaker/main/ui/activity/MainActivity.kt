package com.example.playlistmaker.main.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.activity.MediaActivity
import com.example.playlistmaker.settings.ui.activity.SettingsActivity
import com.example.playlistmaker.ui.root.RootActivity // Используем RootActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Сразу переходим к RootActivity
        val intent = Intent(this, RootActivity::class.java)
        startActivity(intent)
        finish()
    }
}