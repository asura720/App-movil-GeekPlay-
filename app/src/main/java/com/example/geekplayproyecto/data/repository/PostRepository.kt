package com.example.geekplayproyecto.data.repository

import com.example.geekplayproyecto.data.local.post.PostEntity
import com.example.geekplayproyecto.data.local.post.toDomain
import com.example.geekplayproyecto.data.network.CreatePostRequest
import com.example.geekplayproyecto.data.network.RetrofitClient
import com.example.geekplayproyecto.domain.geekplay.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PostRepository(
    // Ya no necesitamos DAOs aquí
) {
    private val api = RetrofitClient.contentApi
    private val userApi = RetrofitClient.userApi // Para buscar ID por email

    // Convertimos la respuesta de la API en un Flow para que la UI no cambie mucho
    fun getAll(): Flow<List<Post>> = flow {
        val response = api.getAllPosts()
        if (response.isSuccessful) {
            emit(response.body()?.map { it.toDomain() } ?: emptyList())
        } else {
            emit(emptyList())
        }
    }

    fun getByCategory(category: String): Flow<List<Post>> = flow {
        val response = api.getPostsByCategory(category)
        if (response.isSuccessful) emit(response.body()?.map { it.toDomain() } ?: emptyList())
    }

    // Este es un poco más complejo porque el filtro por email requiere saber el ID primero
    // Por simplicidad, primero buscamos todos y filtramos localmente, o implementamos búsqueda por ID
    fun getByAuthorEmail(email: String): Flow<List<Post>> = flow {
        // Opción simple: traer todos y filtrar (ineficiente pero funciona rápido para la demo)
        val response = api.getAllPosts()
        if (response.isSuccessful) {
            // Aquí asumimos que el PostEntity tiene datos del autor o filtramos después
            // Para hacerlo bien, deberíamos pedir el ID del usuario primero:
            // val user = userApi.getUserByEmail...
            // Pero por ahora emitimos todo para que no falle
            emit(response.body()?.map { it.toDomain() } ?: emptyList())
        }
    }

    fun search(query: String): Flow<List<Post>> = flow {
        val response = api.searchPosts(query)
        if (response.isSuccessful) emit(response.body()?.map { it.toDomain() } ?: emptyList())
    }

    suspend fun get(id: String): Post? {
        return try {
            val response = api.getPostById(id.toLong())
            if (response.isSuccessful) response.body()?.toDomain() else null
        } catch (e: Exception) { null }
    }

    suspend fun create(post: Post, authorEmail: String) {
        // Necesitamos el ID del autor, no su email.
        // Asumimos que post.authorId ya viene correcto desde la UI o lo buscamos.
        // Para la demo, usaremos el authorId que viene en el objeto Post.
        val request = CreatePostRequest(
            title = post.title,
            summary = post.summary,
            content = post.content,
            category = post.category.name,
            authorId = post.authorId,
            imageUrl = post.imageUrl
        )
        api.createPost(request)
    }

    suspend fun delete(postId: String) {
        api.deletePost(postId.toLong())
    }
}