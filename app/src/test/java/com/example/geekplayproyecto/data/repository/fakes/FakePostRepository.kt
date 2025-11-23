package com.example.geekplayproyecto.data.repository.fakes

import com.example.geekplayproyecto.domain.geekplay.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePostRepository {

    private val posts = mutableListOf<Post>()
    private var nextId = 1L

    fun getAll(): Flow<List<Post>> = flow {
        emit(posts.sortedByDescending { it.publishedAt })
    }

    fun getByCategory(category: String): Flow<List<Post>> = flow {
        val filtered = posts.filter { it.category.name == category }
            .sortedByDescending { it.publishedAt }
        emit(filtered)
    }

    fun search(query: String): Flow<List<Post>> = flow {
        val filtered = posts.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.content.contains(query, ignoreCase = true)
        }
        emit(filtered)
    }

    suspend fun get(id: String): Post? {
        return posts.find { it.id == id }
    }

    suspend fun create(post: Post, authorEmail: String) {
        val newPost = post.copy(id = nextId++.toString())
        posts.add(newPost)
    }

    suspend fun delete(postId: String) {
        posts.removeIf { it.id == postId }
    }

    fun clearAll() {
        posts.clear()
        nextId = 1L
    }
}