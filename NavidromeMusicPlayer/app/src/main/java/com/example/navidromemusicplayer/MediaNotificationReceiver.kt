package com.example.navidromemusicplayer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MediaNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.navidromemusicplayer.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.navidromemusicplayer.ACTION_NEXT"
        const val ACTION_PREV = "com.example.navidromemusicplayer.ACTION_PREV"

        fun getPendingIntent(context: Context, action: String): PendingIntent {
            val intent = Intent(context, MediaNotificationReceiver::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(
                context,
                action.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        // 将操作通过广播发送给 MainActivity 处理
        val action = intent.action
        val mainIntent = Intent("MEDIA_ACTION").apply {
            putExtra("action", action)
        }
        context.sendBroadcast(mainIntent)
    }
}
