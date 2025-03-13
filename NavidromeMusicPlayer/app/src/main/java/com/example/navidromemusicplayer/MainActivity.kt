package com.pusic

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化播放器
        player = ExoPlayer.Builder(this).build()

        // 设置曲目列表的布局管理器和适配器
        recyclerViewTracks.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter { track -> playTrack(track) }
        recyclerViewTracks.adapter = trackAdapter

        // 设置播放列表的布局管理器和适配器
        recyclerViewPlaylist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        playlistAdapter = PlaylistAdapter { playlist -> loadPlaylist(playlist) }
        recyclerViewPlaylist.adapter = playlistAdapter

        // 设置按钮点击事件
        btnPlayPause.setOnClickListener { togglePlayPause() }
        btnNext.setOnClickListener { playNext() }
        btnPrev.setOnClickListener { playPrevious() }

        // 加载播放列表
        loadPlaylists()
    }

    private fun playTrack(track: Track) {
        val mediaItem = MediaItem.fromUri(track.streamUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    private fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    private fun playNext() {
        // 实现播放下一首曲目的逻辑
    }

    private fun playPrevious() {
        // 实现播放上一首曲目的逻辑
    }

    private fun loadPlaylists() {
        // 使用协程从服务器加载播放列表
        CoroutineScope(Dispatchers.IO).launch {
            val playlists = fetchPlaylists()
            runOnUiThread {
                playlistAdapter.submitList(playlists)
            }
        }
    }

    private suspend fun fetchPlaylists(): List<Playlist> {
        // 实现从服务器获取播放列表的逻辑
        return emptyList()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}

data class Track(val id: String, val title: String, val streamUrl: String)
data class Playlist(val id: String, val name: String, val tracks: List<Track>)

class TrackAdapter(private val onTrackClick: (Track) -> Unit) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    // 实现适配器的必要方法
}

class PlaylistAdapter(private val onPlaylistClick: (Playlist) -> Unit) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {
    // 实现适配器的必要方法
}
