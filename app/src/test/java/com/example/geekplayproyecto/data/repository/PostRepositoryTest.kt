package com.example.geekplayproyecto.data.repository

import com.example.geekplayproyecto.data.repository.fakes.FakePostRepository
import com.example.geekplayproyecto.domain.geekplay.Category
import com.example.geekplayproyecto.domain.geekplay.Post
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class PostRepositoryTest {

    private lateinit var repository: FakePostRepository

    @Before
    fun setup() {
        repository = FakePostRepository()
    }

    @Test
    fun `create debe agregar post al repositorio`() = runTest {
        // Given
        val post = Post(
            id = "1",
            authorId = 1L,
            title = "Nuevo videojuego anunciado",
            summary = "Resumen del anuncio",
            content = "Contenido completo del post",
            category = Category.VIDEOJUEGOS,
            authorName = "Ricardo",
            publishedAt = System.currentTimeMillis(),
            likes = 0
        )

        // When
        repository.create(post, "ricardo@geekplay.com")
        val posts = repository.getAll().first()

        // Then
        assertEquals(1, posts.size)
        assertEquals("Nuevo videojuego anunciado", posts[0].title)
    }

    @Test
    fun `getAll debe retornar posts ordenados por fecha`() = runTest {
        // Given
        val post1 = Post("1", 1L, "Post 1", "", "", Category.VIDEOJUEGOS, "User", null, 1000L, 0, null)
        val post2 = Post("2", 1L, "Post 2", "", "", Category.PELICULAS, "User", null, 3000L, 0, null)
        val post3 = Post("3", 1L, "Post 3", "", "", Category.SERIES, "User", null, 2000L, 0, null)

        repository.create(post1, "user@test.com")
        repository.create(post2, "user@test.com")
        repository.create(post3, "user@test.com")

        // When
        val posts = repository.getAll().first()

        // Then
        assertEquals(3, posts.size)
        // El más reciente primero (publishedAt = 3000L)
        assertEquals("Post 2", posts[0].title)
        assertEquals("Post 3", posts[1].title)
        assertEquals("Post 1", posts[2].title)
    }

    @Test
    fun `getByCategory debe filtrar posts correctamente`() = runTest {
        // Given
        val post1 = Post("1", 1L, "Videojuego 1", "", "", Category.VIDEOJUEGOS, "User", null, 1000L, 0, null)
        val post2 = Post("2", 1L, "Película 1", "", "", Category.PELICULAS, "User", null, 2000L, 0, null)
        val post3 = Post("3", 1L, "Videojuego 2", "", "", Category.VIDEOJUEGOS, "User", null, 3000L, 0, null)

        repository.create(post1, "user@test.com")
        repository.create(post2, "user@test.com")
        repository.create(post3, "user@test.com")

        // When
        val videojuegos = repository.getByCategory("VIDEOJUEGOS").first()

        // Then
        assertEquals(2, videojuegos.size)
        assertTrue(videojuegos.all { it.category == Category.VIDEOJUEGOS })
    }

    @Test
    fun `search debe encontrar posts por titulo`() = runTest {
        // Given
        val post1 = Post("1", 1L, "Zelda Tears of the Kingdom", "", "", Category.VIDEOJUEGOS, "User", null, 1000L, 0, null)
        val post2 = Post("2", 1L, "God of War Ragnarok", "", "", Category.VIDEOJUEGOS, "User", null, 2000L, 0, null)

        repository.create(post1, "user@test.com")
        repository.create(post2, "user@test.com")

        // When
        val results = repository.search("Zelda").first()

        // Then
        assertEquals(1, results.size)
        assertEquals("Zelda Tears of the Kingdom", results[0].title)
    }

    @Test
    fun `search debe encontrar posts por contenido`() = runTest {
        // Given
        val post = Post("1", 1L, "Título", "", "contenido con palabra especial", Category.COMICS, "User", null, 1000L, 0, null)
        repository.create(post, "user@test.com")

        // When
        val results = repository.search("especial").first()

        // Then
        assertEquals(1, results.size)
    }

    @Test
    fun `delete debe eliminar post del repositorio`() = runTest {
        // Given
        val post = Post("1", 1L, "Post a eliminar", "", "", Category.COMICS, "User", null, 1000L, 0, null)
        repository.create(post, "user@test.com")

        // When
        repository.delete("1")
        val posts = repository.getAll().first()

        // Then
        assertEquals(0, posts.size)
    }

    @Test
    fun `get debe retornar post por id`() = runTest {
        // Given
        val post = Post("1", 1L, "Post específico", "", "", Category.SERIES, "User", null, 1000L, 0, null)
        repository.create(post, "user@test.com")

        // When
        val found = repository.get("1")

        // Then
        assertNotNull(found)
        assertEquals("Post específico", found?.title)
    }

    @Test
    fun `get con id inexistente debe retornar null`() = runTest {
        // When
        val found = repository.get("999")

        // Then
        assertNull(found)
    }
}