package com.example.geekplayproyecto.data.network

import com.example.geekplayproyecto.data.local.ban.BanNotification
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.DELETE

// DTO para banear
data class ModerationRequest(
    val userId: Long,
    val adminId: Long, // Opcional si tu backend lo requiere
    val contentId: String,
    val type: String, // "POST" o "COMMENT"
    val reason: String,
    val durationMinutes: Int
)

interface ModerationApiService {
    @GET("api/moderation/notifications/user/{userId}")
    suspend fun getNotifications(@Path("userId") userId: Long): Response<List<BanNotification>>

    @POST("api/moderation/action")
    suspend fun performAction(@Body request: ModerationRequest): Response<Void>

    // ✅ NUEVO
    @DELETE("api/moderation/notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: Long): Response<Void>
}
