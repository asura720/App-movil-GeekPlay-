package com.example.geekplayproyecto.data.repository

import com.example.geekplayproyecto.data.local.like.toDomain
import com.example.geekplayproyecto.data.network.RetrofitClient
import com.example.geekplayproyecto.data.network.ToggleLikeRequest
import kotlinx.coroutines.flow.flow

class LikeRepository {
    private val api = RetrofitClient.interactionApi

    fun getLikesForPost(postId: String) = flow {
        try {
            val response = api.getLikes(postId.toLong())
            if (response.isSuccessful) {
                emit(response.body()?.map { it.toDomain() } ?: emptyList())
            }
        } catch (e: Exception) { emit(emptyList()) }
    }

    suspend fun toggleLike(postId: String, userEmail: String) {
        api.toggleLike(postId.toLong(), ToggleLikeRequest(userEmail))
    }
}