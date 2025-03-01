package com.example.playlistmaker.search.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import java.util.Locale

class TrackAdapter(
    private var tracks: List<Track>,
    private val onItemClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackNameTextView: TextView = itemView.findViewById(R.id.trackName)
        private val artistNameTextView: TextView = itemView.findViewById(R.id.artistName)
        private val trackTimeTextView: TextView = itemView.findViewById(R.id.trackDuration)
        private val artworkImageView: ImageView = itemView.findViewById(R.id.trackImage)

        fun bind(track: Track) {
            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            trackTimeTextView.text = formatTrackTime(track.trackTimeMillis)

            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .transform(RoundedCorners(8))
                .into(artworkImageView)

            itemView.setOnClickListener {
                onItemClick(track)
            }

        }

        private fun formatTrackTime(trackTimeMillis: Long): String {
            val minutes = (trackTimeMillis / 60000)
            val seconds = (trackTimeMillis % 60000 / 1000)
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_track_items, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}