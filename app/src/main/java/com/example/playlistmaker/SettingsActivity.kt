package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.button_back)
//        val settingsPush = findViewById<LinearLayout>(R.id.share_app) Для себя проверил будет ли работать нажатие

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

//        settingsPush.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        } Для себя проверил будет ли работать нажатие
    }
}



