package com.example.geekplayproyecto.data.repository

import com.example.geekplayproyecto.data.repository.fakes.FakeUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class UserRepositoryTest {

    private lateinit var repository: FakeUserRepository

    @Before
    fun setup() {
        repository = FakeUserRepository()
    }

    @Test
    fun `register con datos validos debe crear usuario`() = runTest {
        // Given
        val name = "Ricardo"
        val email = "ricardo@geekplay.com"
        val phone = "912345678"
        val pass = "GeekPlay123@"

        // When
        val result = repository.register(name, email, phone, pass)

        // Then
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `register con email duplicado debe fallar`() = runTest {
        // Given
        repository.register("Usuario1", "ricardo@geekplay.com", "912345678", "Pass123@")

        // When
        val result = repository.register("Usuario2", "ricardo@geekplay.com", "987654321", "Pass456@")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error al registrar", result.exceptionOrNull()?.message)
    }

    @Test
    fun `login con credenciales correctas debe retornar usuario`() = runTest {
        // Given
        repository.register("Ricardo", "ricardo@geekplay.com", "912345678", "GeekPlay123@")

        // When
        val result = repository.login("ricardo@geekplay.com", "GeekPlay123@")

        // Then
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("Ricardo", user?.name)
        assertEquals("ricardo@geekplay.com", user?.email)
    }

    @Test
    fun `login con email inexistente debe fallar`() = runTest {
        // When
        val result = repository.login("noexiste@geekplay.com", "Pass123@")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Credenciales incorrectas", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getById con id existente debe retornar usuario`() = runTest {
        // Given
        val resultRegister = repository.register("Ricardo", "ricardo@geekplay.com", "912345678", "Pass123@")
        val userId = resultRegister.getOrNull()!!

        // When
        val user = repository.getById(userId)

        // Then
        assertNotNull(user)
        assertEquals("Ricardo", user?.name)
    }

    @Test
    fun `getById con id inexistente debe retornar null`() = runTest {
        // When
        val user = repository.getById(999L)

        // Then
        assertNull(user)
    }

    @Test
    fun `updateProfile debe actualizar datos del usuario`() = runTest {
        // Given
        val resultRegister = repository.register("Ricardo", "ricardo@geekplay.com", "912345678", "Pass123@")
        val userId = resultRegister.getOrNull()!!

        // When
        val result = repository.updateProfile(userId, "Ricardo García", "987654321", "/path/image.jpg")

        // Then
        assertTrue(result.isSuccess)
        val updated = result.getOrNull()
        assertEquals("Ricardo García", updated?.name)
        assertEquals("987654321", updated?.phone)
        assertEquals("/path/image.jpg", updated?.profileImagePath)
    }

    @Test
    fun `changePassword con usuario existente debe retornar success`() = runTest {
        // Given
        val resultRegister = repository.register("Ricardo", "ricardo@geekplay.com", "912345678", "OldPass123@")
        val userId = resultRegister.getOrNull()!!

        // When
        val result = repository.changePassword(userId, "OldPass123@", "NewPass456@")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Contraseña actualizada", result.getOrNull())
    }
}