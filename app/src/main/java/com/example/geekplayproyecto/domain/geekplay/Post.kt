package com.example.geekplayproyecto.domain.geekplay

// Modelo de dominio de un post de GeekPlay
data class Post(
    val id: String,
    val authorId: Long,
    val title: String,
    val summary: String,
    val content: String,
    val category: Category,
    val authorName: String,
    val authorProfileImageUrl: String? = null,
    val publishedAt: Long,
    val likes: Int = 0,
    val imageUrl: String? = null
)
