package com.example.geekplayproyecto.data.local.ban

import com.example.geekplayproyecto.data.network.ModerationRequest
import com.example.geekplayproyecto.data.network.RetrofitClient
import kotlinx.coroutines.flow.flow

class BanNotificationRepository {
    private val api = RetrofitClient.moderationApi

    fun getBanNotificationsByUser(userId: Long) = flow {
        try {
            val response = api.getNotifications(userId)
            if (response.isSuccessful) {
                emit(response.body() ?: emptyList())
            }
        } catch (e: Exception) { emit(emptyList()) }
    }

    // ✅ NUEVO MÉTODO: Recibe los datos directos para la acción
    suspend fun performModeration(
        targetUserId: Long,
        contentId: String,
        type: String, // "POST" o "COMMENT"
        reason: String,
        durationMinutes: Int
    ) {
        // Aquí asumimos adminId = 1 por simplicidad en la demo,
        // en un futuro podrías pasarlo como argumento.
        val request = ModerationRequest(
            userId = targetUserId,
            adminId = 1,
            contentId = contentId,
            type = type,
            reason = reason,
            durationMinutes = durationMinutes
        )
        api.performAction(request)
    }

    // ✅ NUEVO
    suspend fun delete(notificationId: Long) {
        try {
            api.deleteNotification(notificationId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}