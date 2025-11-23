package com.example.geekplayproyecto.data.repository.fakes

import com.example.geekplayproyecto.data.local.comment.Comment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCommentRepository {

    private val comments = mutableListOf<Comment>()
    private var nextId = 1L

    fun observeByPost(postId: String): Flow<List<Comment>> = flow {
        val filtered = comments.filter { it.postId == postId }
            .sortedByDescending { it.timestamp }
        emit(filtered)
    }

    suspend fun addComment(postId: String, authorId: Long, content: String) {
        val newComment = Comment(
            id = nextId++.toString(),
            postId = postId,
            authorId = authorId,
            authorName = "Usuario $authorId",
            authorEmail = "",
            authorProfileImageUrl = null,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        comments.add(newComment)
    }

    suspend fun deleteComment(commentId: String) {
        comments.removeIf { it.id == commentId }
    }

    fun clearAll() {
        comments.clear()
        nextId = 1L
    }
}