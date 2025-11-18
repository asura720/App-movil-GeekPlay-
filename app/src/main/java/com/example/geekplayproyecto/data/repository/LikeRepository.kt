package com.example.geekplayproyecto.data.repository

import com.example.geekplayproyecto.data.local.like.LikeDao
import com.example.geekplayproyecto.data.local.like.LikeEntity
import com.example.geekplayproyecto.data.local.like.toDomain
import kotlinx.coroutines.flow.map

class LikeRepository(private val likeDao: LikeDao) {

    fun getLikesForPost(postId: String) = likeDao.observeByPost(postId).map { list ->
        list.map { it.toDomain() }
    }

    suspend fun toggleLike(postId: String, userEmail: String) {
        val existingLike = likeDao.getUserLike(postId, userEmail)
        if (existingLike != null) {
            likeDao.delete(existingLike)
        } else {
            val like = LikeEntity(postId = postId, userEmail = userEmail)
            likeDao.insert(like)
        }
    }
}