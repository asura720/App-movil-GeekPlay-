package com.example.geekplayproyecto.data.local.post

import com.example.geekplayproyecto.domain.geekplay.Category
import com.example.geekplayproyecto.domain.geekplay.Post
import java.text.SimpleDateFormat
import java.util.Locale

// DTO: Lo que llega del JSON del Microservicio
data class PostEntity(
    val id: Long,
    val title: String,
    val summary: String,
    val content: String,
    val category: String,
    val authorId: Long,
    val publishedAt: String, // ⚠️ El servidor ahora lo manda como texto
    val imageUrl: String?,
    val authorName: String? = null, // Campos enriquecidos opcionales
    val authorProfileImageUrl: String? = null,
    val likes: Int = 0
)

// Mapper: Convierte el DTO de red al modelo de Dominio de la App
fun PostEntity.toDomain(): Post {
    // Convertimos la fecha texto de vuelta a Long para que funcione el "hace x minutos" de la UI
    val timestamp = try {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        format.parse(this.publishedAt)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }

    return Post(
        id = this.id.toString(), // La UI usa String para navegación
        authorId = this.authorId,
        title = this.title,
        summary = this.summary,
        content = this.content,
        category = try { Category.valueOf(this.category) } catch (e: Exception) { Category.VIDEOJUEGOS },
        authorName = this.authorName ?: "Usuario",
        authorProfileImageUrl = this.authorProfileImageUrl,
        publishedAt = timestamp,
        likes = this.likes,
        imageUrl = this.imageUrl
    )
}