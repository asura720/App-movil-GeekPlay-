package com.example.geekplayproyecto.data.repository

import com.example.geekplayproyecto.data.repository.fakes.FakeCommentRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class CommentRepositoryTest {

    private lateinit var repository: FakeCommentRepository

    @Before
    fun setup() {
        repository = FakeCommentRepository()
    }

    @Test
    fun `addComment debe agregar comentario al post`() = runTest {
        // Given
        val postId = "1"
        val authorId = 1L
        val content = "Gran post!"

        // When
        repository.addComment(postId, authorId, content)
        val comments = repository.observeByPost(postId).first()

        // Then
        assertEquals(1, comments.size)
        assertEquals(content, comments[0].content)
        assertEquals(authorId, comments[0].authorId)
    }

    @Test
    fun `observeByPost debe retornar solo comentarios del post especificado`() = runTest {
        // Given
        repository.addComment("1", 1L, "Comentario en post 1")
        repository.addComment("2", 2L, "Comentario en post 2")
        repository.addComment("1", 3L, "Otro comentario en post 1")

        // When
        val commentsPost1 = repository.observeByPost("1").first()

        // Then
        assertEquals(2, commentsPost1.size)
        assertTrue(commentsPost1.all { it.postId == "1" })
    }

    @Test
    fun `observeByPost debe retornar comentarios ordenados por timestamp descendente`() = runTest {
        // Given - Se agregan en orden, el último tiene el timestamp más reciente
        repository.addComment("1", 1L, "Primer comentario")
        Thread.sleep(10) // Pequeña pausa para asegurar timestamps diferentes
        repository.addComment("1", 2L, "Segundo comentario")
        Thread.sleep(10)
        repository.addComment("1", 3L, "Tercer comentario")

        // When
        val comments = repository.observeByPost("1").first()

        // Then
        assertEquals(3, comments.size)
        // El más reciente primero
        assertEquals("Tercer comentario", comments[0].content)
        assertEquals("Segundo comentario", comments[1].content)
        assertEquals("Primer comentario", comments[2].content)
    }

    @Test
    fun `deleteComment debe eliminar comentario especifico`() = runTest {
        // Given
        repository.addComment("1", 1L, "Comentario 1")
        repository.addComment("1", 2L, "Comentario 2")
        val comments = repository.observeByPost("1").first()
        val idToDelete = comments[0].id

        // When
        repository.deleteComment(idToDelete)
        val remainingComments = repository.observeByPost("1").first()

        // Then
        assertEquals(1, remainingComments.size)
        assertFalse(remainingComments.any { it.id == idToDelete })
    }

    @Test
    fun `observeByPost en post sin comentarios debe retornar lista vacia`() = runTest {
        // When
        val comments = repository.observeByPost("999").first()

        // Then
        assertTrue(comments.isEmpty())
    }

    @Test
    fun `addComment debe generar IDs unicos autoincrementales`() = runTest {
        // Given
        repository.addComment("1", 1L, "Comentario 1")
        repository.addComment("1", 1L, "Comentario 2")
        repository.addComment("1", 1L, "Comentario 3")

        // When
        val comments = repository.observeByPost("1").first()

        // Then
        val ids = comments.map { it.id }
        assertEquals(3, ids.distinct().size) // Todos los IDs son únicos
    }
}