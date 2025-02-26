package com.example.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.shareTheApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.writeToTheSupport.setOnClickListener {
            viewModel.writeToSupport(
                getString(R.string.email_recipient),
                getString(R.string.email_subject),
                getString(R.string.email_body)
            )
        }

        binding.userAgreement.setOnClickListener {
            viewModel.openUserAgreement(getString(R.string.user_agreement_url))
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }
    }

    private fun observeViewModel() {
        viewModel.darkThemeEnabled.observe(viewLifecycleOwner) { isDarkTheme ->
            binding.themeSwitcher.isChecked = isDarkTheme
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}