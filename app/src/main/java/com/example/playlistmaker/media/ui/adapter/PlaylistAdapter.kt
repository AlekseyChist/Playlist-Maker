// app/src/main/java/com/example/playlistmaker/media/ui/adapter/PlaylistAdapter.kt
package com.example.playlistmaker.media.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.media.domain.model.Playlist
import java.io.File

class PlaylistAdapter(
    private val onPlaylistClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaylistViewHolder(
        private val binding: ItemPlaylistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.tracksCount.text = "${playlist.trackCount} треков" // Временно без plurals

            // Загрузка обложки
            if (!playlist.coverPath.isNullOrEmpty()) {
                val coverFile =
                    File(binding.root.context.getExternalFilesDir(null), playlist.coverPath)
                Glide.with(binding.root)
                    .load(coverFile)
                    .placeholder(R.drawable.placeholder_image) // Используем существующий
                    .error(R.drawable.placeholder_image) // На случай ошибки
                    .transform(CenterCrop(), RoundedCorners(8))
                    .into(binding.playlistCover)
            } else {
                binding.playlistCover.setImageResource(R.drawable.placeholder_image)
            }

            binding.root.setOnClickListener {
                onPlaylistClick(playlist)
            }
        }
    }

    class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem == newItem
        }
    }
}