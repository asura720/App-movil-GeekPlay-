package com.example.geekplayproyecto.data.repository

import com.example.geekplayproyecto.data.repository.fakes.FakeLikeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class LikeRepositoryTest {

    private lateinit var repository: FakeLikeRepository

    @Before
    fun setup() {
        repository = FakeLikeRepository()
    }

    @Test
    fun `toggleLike en post sin like debe agregar like`() = runTest {
        // Given
        val postId = "1"
        val userEmail = "ricardo@geekplay.com"

        // When
        repository.toggleLike(postId, userEmail)
        val likes = repository.getLikesForPost(postId).first()

        // Then
        assertEquals(1, likes.size)
        assertEquals(userEmail, likes[0].userEmail)
    }

    @Test
    fun `toggleLike en post con like debe quitar like`() = runTest {
        // Given
        val postId = "1"
        val userEmail = "ricardo@geekplay.com"
        repository.toggleLike(postId, userEmail) // Agregar like

        // When
        repository.toggleLike(postId, userEmail) // Quitar like
        val likes = repository.getLikesForPost(postId).first()

        // Then
        assertEquals(0, likes.size)
    }

    @Test
    fun `toggleLike debe funcionar como switch on-off`() = runTest {
        // Given
        val postId = "1"
        val userEmail = "ricardo@geekplay.com"

        // When - Ciclo: agregar, quitar, agregar
        repository.toggleLike(postId, userEmail)
        var likes = repository.getLikesForPost(postId).first()
        assertEquals(1, likes.size) // Agregado

        repository.toggleLike(postId, userEmail)
        likes = repository.getLikesForPost(postId).first()
        assertEquals(0, likes.size) // Quitado

        repository.toggleLike(postId, userEmail)
        likes = repository.getLikesForPost(postId).first()
        assertEquals(1, likes.size) // Agregado de nuevo
    }

    @Test
    fun `getLikesForPost debe retornar solo likes del post especificado`() = runTest {
        // Given
        repository.toggleLike("1", "user1@test.com")
        repository.toggleLike("1", "user2@test.com")
        repository.toggleLike("2", "user3@test.com")

        // When
        val likesPost1 = repository.getLikesForPost("1").first()

        // Then
        assertEquals(2, likesPost1.size)
        assertTrue(likesPost1.all { it.postId == "1" })
    }

    @Test
    fun `getLikesForPost en post sin likes debe retornar lista vacia`() = runTest {
        // When
        val likes = repository.getLikesForPost("999").first()

        // Then
        assertTrue(likes.isEmpty())
    }

    @Test
    fun `multiples usuarios pueden dar like al mismo post`() = runTest {
        // Given
        val postId = "1"

        // When
        repository.toggleLike(postId, "user1@test.com")
        repository.toggleLike(postId, "user2@test.com")
        repository.toggleLike(postId, "user3@test.com")

        val likes = repository.getLikesForPost(postId).first()

        // Then
        assertEquals(3, likes.size)
        val emails = likes.map { it.userEmail }
        assertTrue(emails.contains("user1@test.com"))
        assertTrue(emails.contains("user2@test.com"))
        assertTrue(emails.contains("user3@test.com"))
    }

    @Test
    fun `un usuario solo puede dar un like por post`() = runTest {
        // Given
        val postId = "1"
        val userEmail = "ricardo@geekplay.com"

        // When - Intentar dar like 3 veces
        repository.toggleLike(postId, userEmail)
        repository.toggleLike(postId, userEmail) // Quita
        repository.toggleLike(postId, userEmail) // Agrega de nuevo

        val likes = repository.getLikesForPost(postId).first()

        // Then
        assertEquals(1, likes.size) // Solo 1 like
        assertEquals(userEmail, likes[0].userEmail)
    }
}