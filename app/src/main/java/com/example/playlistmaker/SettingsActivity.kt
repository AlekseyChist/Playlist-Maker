package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
        val shareButton = findViewById<TextView>(R.id.share_the_app)
        val supportButton = findViewById<TextView>(R.id.write_to_the_support)
        val userAgreementButton = findViewById<TextView>(R.id.user_agreement)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        shareButton.setOnClickListener {
            shareApp()
        }

        supportButton.setOnClickListener {
            writeToSupport()
        }

        userAgreementButton.setOnClickListener {
            openUserAgreement()
        }

    }

    private fun openUserAgreement() {
        val url = getString(R.string.user_agreement_url)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    private fun writeToSupport() {
        val email = getString(R.string.email_recipient)
        val subject = getString(R.string.email_subject)
        val body = getString(R.string.email_body)

        val intentMail = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(intentMail)
    }

    private fun shareApp() {
        val message = getString(R.string.app_share_message)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareChooser = Intent.createChooser(shareIntent, null)
        startActivity(shareChooser)
    }
}



