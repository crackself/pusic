package com.example.navidromemusicplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlaylistAdapter(private val onItemClick: (Track) -> Unit) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private var playlist: List<Track> = listOf()

    fun submitList(list: List<Track>) {
        playlist = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val track = playlist[position]
        holder.bind(track, onItemClick)
    }

    override fun getItemCount(): Int = playlist.size

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.trackTitlePlaylist)
        fun bind(track: Track, clickListener: (Track) -> Unit) {
            titleTextView.text = track.title
            itemView.setOnClickListener { clickListener(track) }
        }
    }
}
