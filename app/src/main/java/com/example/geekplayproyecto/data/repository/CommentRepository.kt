package com.example.geekplayproyecto.data.repository

import com.example.geekplayproyecto.data.local.comment.toDomain
import com.example.geekplayproyecto.data.network.CreateCommentRequest
import com.example.geekplayproyecto.data.network.RetrofitClient
import kotlinx.coroutines.flow.flow

class CommentRepository {
    private val api = RetrofitClient.interactionApi

    fun observeByPost(postId: String) = flow {
        try {
            val response = api.getComments(postId.toLong())
            if (response.isSuccessful) {
                emit(response.body()?.map { it.toDomain() } ?: emptyList())
            }
        } catch (e: Exception) { emit(emptyList()) }
    }

    suspend fun addComment(postId: String, authorId: Long, content: String) {
        api.addComment(CreateCommentRequest(postId.toLong(), authorId, content))
    }

    suspend fun deleteComment(commentId: String) {
        api.deleteComment(commentId)
    }
}