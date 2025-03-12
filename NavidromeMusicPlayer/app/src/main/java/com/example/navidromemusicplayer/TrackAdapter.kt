package com.example.navidromemusicplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private val onPlay: (Track) -> Unit,
    private val onAddToPlaylist: (Track) -> Unit,
    private val onDownload: (Track) -> Unit
) : ListAdapter<Track, TrackAdapter.TrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track, onPlay, onAddToPlaylist, onDownload)
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.trackTitle)
        private val btnAdd: ImageButton = itemView.findViewById(R.id.btnAddPlaylist)
        private val btnDownload: ImageButton = itemView.findViewById(R.id.btnDownload)
        fun bind(
            track: Track,
            onPlay: (Track) -> Unit,
            onAddToPlaylist: (Track) -> Unit,
            onDownload: (Track) -> Unit
        ) {
            titleTextView.text = track.title
            itemView.setOnClickListener { onPlay(track) }
            btnAdd.setOnClickListener { onAddToPlaylist(track) }
            btnDownload.setOnClickListener { onDownload(track) }
        }
    }
}

class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean =
        oldItem == newItem
}
