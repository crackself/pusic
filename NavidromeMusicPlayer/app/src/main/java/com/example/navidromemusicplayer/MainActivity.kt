package com.example.navidromemusicplayer

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var player: ExoPlayer
    private val playlist = mutableListOf<Track>()
    private var currentTrack: Track? = null
    private var lastPlaybackPosition: Long = 0L
    private lateinit var sp: SharedPreferences

    companion object {
        const val CHANNEL_ID = "music_channel"
        const val NOTIFICATION_ID = 1
    }

    // 接收通知栏操作广播
    private val mediaActionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("action")) {
                MediaNotificationReceiver.ACTION_PLAY_PAUSE -> togglePlayPause()
                MediaNotificationReceiver.ACTION_NEXT -> playNext()
                MediaNotificationReceiver.ACTION_PREV -> playPrevious()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(mediaActionReceiver, IntentFilter("MEDIA_ACTION"))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(mediaActionReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = getSharedPreferences("config", MODE_PRIVATE)
        // 检查是否已登录（即是否配置了服务器地址）
        val serverUrl = sp.getString("server_url", null)
        if (serverUrl.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        createNotificationChannel()

        // 初始化曲目列表 RecyclerView
        trackAdapter = TrackAdapter(
            onPlay = { track -> playTrack(track) },
            onAddToPlaylist = { track -> addToPlaylist(track) },
            onDownload = { track -> downloadTrack(track) }
        )
        recyclerViewTracks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = trackAdapter
        }

        // 初始化播放列表 RecyclerView（水平排列）
        playlistAdapter = PlaylistAdapter { track -> playTrack(track) }
        recyclerViewPlaylist.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = playlistAdapter
        }

        // 初始化播放控制按钮
        btnPlayPause.setOnClickListener { togglePlayPause() }
        btnNext.setOnClickListener { playNext() }
        btnPrev.setOnClickListener { playPrevious() }

        // 初始化 ExoPlayer 播放器
        player = ExoPlayer.Builder(this).build()

        // 尝试恢复断点续播（保存于 SharedPreferences）
        lastPlaybackPosition = sp.getLong("last_playback_position", 0L)

        // 加载曲目列表
        fetchTracks()
    }

    private fun fetchTracks() {
        val serverUrl = sp.getString("server_url", "")!!
        val baseUrl = if (serverUrl.endsWith("/")) serverUrl else "$serverUrl/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(NavidromeApi::class.java)
        lifecycleScope.launch {
            try {
                val tracks = api.getTracks()
                trackAdapter.submitList(tracks)
            } catch (e: Exception) {
                e.printStackTrace()
                // 若请求失败，使用示例数据
                trackAdapter.submitList(dummyTracks())
            }
        }
    }

    private fun dummyTracks(): List<Track> {
        return listOf(
            Track("1", "示例曲目 1", "https://your-navidrome-server.com/stream/1"),
            Track("2", "示例曲目 2", "https://your-navidrome-server.com/stream/2")
        )
    }

    private fun playTrack(track: Track) {
        currentTrack = track
        val mediaItem = MediaItem.fromUri(track.streamUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        // 若当前曲目与上次保存的一致，则恢复播放位置
        val savedTrackId = sp.getString("last_track_id", null)
        if (savedTrackId == track.id) {
            player.seekTo(lastPlaybackPosition)
        }
        player.play()
        updateNotification(track, true)
    }

    private fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
            updateNotification(currentTrack, false)
        } else {
            player.play()
            updateNotification(currentTrack, true)
        }
    }

    private fun playNext() {
        // 简单示例：若播放列表不为空，则播放列表中下一曲
        currentTrack?.let { current ->
            val index = playlist.indexOfFirst { it.id == current.id }
            if (index != -1 && index < playlist.size - 1) {
                playTrack(playlist[index + 1])
            } else {
                Toast.makeText(this, "没有下一曲", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playPrevious() {
        currentTrack?.let { current ->
            val index = playlist.indexOfFirst { it.id == current.id }
            if (index > 0) {
                playTrack(playlist[index - 1])
            } else {
                Toast.makeText(this, "没有上一曲", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addToPlaylist(track: Track) {
        playlist.add(track)
        // 更新播放列表 RecyclerView
        playlistAdapter.submitList(playlist.toList())
        Toast.makeText(this, "已加入播放列表", Toast.LENGTH_SHORT).show()
    }

    private fun downloadTrack(track: Track) {
        val request = DownloadManager.Request(Uri.parse(track.streamUrl))
            .setTitle(track.title)
            .setDescription("Downloading music")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${track.title}.mp3")
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
        Toast.makeText(this, "开始下载: ${track.title}", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Music Playback", NotificationManager.IMPORTANCE_LOW)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(track: Track?, isPlaying: Boolean) {
        if (track == null) return
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(track.title)
            .setContentText(if (isPlaying) "正在播放" else "已暂停")
            .setOngoing(isPlaying)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            // 添加“上一曲”、“播放/暂停”、“下一曲”按钮
            .addAction(
                android.R.drawable.ic_media_previous,
                "上一曲",
                MediaNotificationReceiver.getPendingIntent(this, MediaNotificationReceiver.ACTION_PREV)
            )
            .addAction(
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                "播放/暂停",
                MediaNotificationReceiver.getPendingIntent(this, MediaNotificationReceiver.ACTION_PLAY_PAUSE)
            )
            .addAction(
                android.R.drawable.ic_media_next,
                "下一曲",
                MediaNotificationReceiver.getPendingIntent(this, MediaNotificationReceiver.ACTION_NEXT)
            )
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, builder.build())
    }

    override fun onPause() {
        super.onPause()
        // 保存当前播放曲目及播放位置，用于断点续播
        currentTrack?.let {
            sp.edit().apply {
                putString("last_track_id", it.id)
                putLong("last_playback_position", player.currentPosition)
                apply()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
