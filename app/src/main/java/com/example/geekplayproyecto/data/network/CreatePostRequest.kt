package com.example.geekplayproyecto.data.network

import com.example.geekplayproyecto.data.local.post.PostEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// DTO para crear un post (lo que enviamos)
data class CreatePostRequest(
    val title: String,
    val summary: String,
    val content: String,
    val category: String,
    val authorId: Long,
    val imageUrl: String?
)

interface ContentApiService {
    @GET("api/posts")
    suspend fun getAllPosts(): Response<List<PostEntity>>

    @GET("api/posts/category/{category}")
    suspend fun getPostsByCategory(@Path("category") category: String): Response<List<PostEntity>>

    @GET("api/posts/author/{authorId}")
    suspend fun getPostsByAuthor(@Path("authorId") authorId: Long): Response<List<PostEntity>>

    @GET("api/posts/search")
    suspend fun searchPosts(@Query("query") query: String): Response<List<PostEntity>>

    @GET("api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Response<PostEntity>

    @POST("api/posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<PostEntity>

    @DELETE("api/posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Response<Void>
}