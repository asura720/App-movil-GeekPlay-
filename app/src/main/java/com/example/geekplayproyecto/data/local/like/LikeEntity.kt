package com.example.geekplayproyecto.data.local.like

// ✅ 1. MODELO DE DOMINIO
data class Like(
    val postId: String,
    val userEmail: String,
    val userName: String,
    val userProfileImageUrl: String?,
    val timestamp: Long
)

// ✅ 2. DTO (JSON del Servidor)
data class LikeEntity(
    val postId: Long,
    val userEmail: String,
    val userName: String? = "Usuario",
    val userProfileImageUrl: String? = null
)

// ✅ 3. MAPPER
fun LikeEntity.toDomain() = Like(
    postId = postId.toString(),
    userEmail = userEmail,
    userName = userName ?: "Usuario",
    userProfileImageUrl = userProfileImageUrl,
    timestamp = System.currentTimeMillis() // La API de likes no manda fecha, usamos la actual
)