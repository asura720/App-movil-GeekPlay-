package com.example.geekplayproyecto.data.local.comment

import java.text.SimpleDateFormat
import java.util.Locale

// ✅ 1. MODELO DE DOMINIO (El que usa la UI)
data class Comment(
    val id: String,
    val postId: String,
    val authorId: Long,
    val authorName: String,
    val authorEmail: String, // Campo opcional o vacío si la API no lo manda
    val authorProfileImageUrl: String?,
    val content: String,
    val timestamp: Long
)

// ✅ 2. DTO (El que llega del JSON de Retrofit)
data class CommentEntity(
    val id: String,
    val postId: Long,
    val authorId: Long,
    val authorName: String?,
    val authorProfileImageUrl: String?,
    val content: String,
    val timestamp: String // Fecha legible que manda el servidor
)

// ✅ 3. MAPPER (Convierte de DTO a Dominio)
fun CommentEntity.toDomain(): Comment {
    // Convertir la fecha String ("dd/MM/yyyy HH:mm:ss") a Long para la UI
    val timeLong = try {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        format.parse(this.timestamp)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }

    return Comment(
        id = this.id,
        postId = this.postId.toString(),
        authorId = this.authorId,
        authorName = this.authorName ?: "Anónimo",
        authorEmail = "", // La API pública de interacción no devuelve el email, lo dejamos vacío
        authorProfileImageUrl = this.authorProfileImageUrl,
        content = this.content,
        timestamp = timeLong
    )
}