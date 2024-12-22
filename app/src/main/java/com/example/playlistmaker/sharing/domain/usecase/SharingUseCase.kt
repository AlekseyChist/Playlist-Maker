package com.example.playlistmaker.sharing.domain.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R


interface SharingUseCase {
    fun shareApp()
    fun writeToSupport(email: String, subject: String, body: String)
    fun openUserAgreement(url: String)
}

class SharingUseCaseImpl(
    private val context: Context
) : SharingUseCase {

    override fun shareApp() {
        val message = context.getString(R.string.app_share_message)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Добавляем флаг
        }
        context.startActivity(Intent.createChooser(shareIntent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun writeToSupport(email: String, subject: String, body: String) {
        val intentMail = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Добавляем флаг
        }
        context.startActivity(intentMail)
    }

    override fun openUserAgreement(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Добавляем флаг




































































        }
        context.startActivity(intent)
    }
}