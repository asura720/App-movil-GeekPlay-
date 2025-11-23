package com.example.geekplayproyecto.data.network

import com.example.geekplayproyecto.data.local.comment.CommentEntity
import com.example.geekplayproyecto.data.local.like.LikeEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// DTOs para enviar
data class CreateCommentRequest(val postId: Long, val authorId: Long, val content: String)
data class ToggleLikeRequest(val userEmail: String)

interface InteractionApiService {
    // --- Comentarios ---
    @GET("api/interactions/posts/{postId}/comments")
    suspend fun getComments(@Path("postId") postId: Long): Response<List<CommentEntity>>

    @POST("api/interactions/comments")
    suspend fun addComment(@Body request: CreateCommentRequest): Response<CommentEntity>

    @DELETE("api/interactions/comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: String): Response<Void>

    // --- Likes ---
    @GET("api/interactions/posts/{postId}/likes")
    suspend fun getLikes(@Path("postId") postId: Long): Response<List<LikeEntity>>

    @POST("api/interactions/posts/{postId}/likes/toggle")
    suspend fun toggleLike(@Path("postId") postId: Long, @Body request: ToggleLikeRequest): Response<Map<String, Boolean>>
}