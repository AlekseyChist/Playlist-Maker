package com.example.playlistmaker.settings.ui.activity


import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        findViewById<ImageView>(R.id.button_back).setOnClickListener { finish() }

        findViewById<TextView>(R.id.share_the_app).setOnClickListener {
            viewModel.shareApp()
        }

        findViewById<TextView>(R.id.write_to_the_support).setOnClickListener {
            viewModel.writeToSupport(
                getString(R.string.email_recipient),
                getString(R.string.email_subject),
                getString(R.string.email_body)
            )
        }

        findViewById<TextView>(R.id.user_agreement).setOnClickListener {
            viewModel.openUserAgreement(getString(R.string.user_agreement_url))
        }

        findViewById<SwitchMaterial>(R.id.themeSwitcher).setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }
    }

    private fun observeViewModel() {
        viewModel.darkThemeEnabled.observe(this) { isDarkTheme ->
            findViewById<SwitchMaterial>(R.id.themeSwitcher).isChecked = isDarkTheme
        }
    }
}