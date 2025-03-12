package com.example.navidromemusicplayer

import retrofit2.http.GET

interface NavidromeApi {
    // 示例接口，实际请根据 Navidrome 或 Subsonic API 文档调整
    @GET("api/tracks")
    suspend fun getTracks(): List<Track>
}
