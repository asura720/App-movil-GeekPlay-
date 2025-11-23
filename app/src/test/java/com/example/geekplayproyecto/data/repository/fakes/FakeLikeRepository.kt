package com.example.geekplayproyecto.data.repository.fakes

import com.example.geekplayproyecto.data.local.like.Like
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLikeRepository {

    private val likes = mutableListOf<Like>()

    fun getLikesForPost(postId: String): Flow<List<Like>> = flow {
        val filtered = likes.filter { it.postId == postId }
        emit(filtered)
    }

    suspend fun toggleLike(postId: String, userEmail: String) {
        val existing = likes.find { it.postId == postId && it.userEmail == userEmail }

        if (existing != null) {
            likes.remove(existing)
        } else {
            likes.add(Like(
                postId = postId,
                userEmail = userEmail,
                userName = "Usuario",
                userProfileImageUrl = null,
                timestamp = System.currentTimeMillis()
            ))
        }
    }

    fun clearAll() {
        likes.clear()
    }
}