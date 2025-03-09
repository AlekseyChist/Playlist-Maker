package com.example.playlistmaker.media.ui.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.media.ui.adapter.MediaViewPagerAdapter
import com.example.playlistmaker.media.ui.fragment.FragmentFavoriteTracks
import com.example.playlistmaker.media.ui.fragment.PlaylistsFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fragments = listOf(
            FragmentFavoriteTracks(),
            PlaylistsFragment()
        )
        val adapter = MediaViewPagerAdapter(supportFragmentManager, lifecycle, fragments)
        binding.viewPager.adapter = adapter


        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }.attach()
    }
}