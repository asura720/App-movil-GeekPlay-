package com.example.geekplayproyecto.data.repository.fakes

import com.example.geekplayproyecto.data.local.user.UserEntity

class FakeUserRepository {

    private val users = mutableListOf<UserEntity>()
    private var nextId = 1L

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = users.find { it.email == email }
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Credenciales incorrectas"))
        }
    }

    suspend fun register(name: String, email: String, phone: String, pass: String): Result<Long> {
        if (users.any { it.email == email }) {
            return Result.failure(Exception("Error al registrar"))
        }

        val newUser = UserEntity(
            id = nextId++,
            name = name,
            email = email,
            phone = phone,
            profileImagePath = null,
            isAdmin = false,
            bannedUntil = null
        )
        users.add(newUser)
        return Result.success(newUser.id)
    }

    suspend fun getById(id: Long): UserEntity? {
        return users.find { it.id == id }
    }

    suspend fun updateProfile(userId: Long, name: String, phone: String, imagePath: String?): Result<UserEntity> {
        val user = users.find { it.id == userId }
        return if (user != null) {
            val updated = user.copy(
                name = name,
                phone = phone,
                profileImagePath = imagePath
            )
            users[users.indexOfFirst { it.id == userId }] = updated
            Result.success(updated)
        } else {
            Result.failure(Exception("Error al actualizar"))
        }
    }

    suspend fun changePassword(userId: Long, current: String, newPass: String): Result<String> {
        val user = users.find { it.id == userId }
        return if (user != null) {
            Result.success("Contraseña actualizada")
        } else {
            Result.failure(Exception("Error: Verifique su contraseña actual"))
        }
    }

    fun clearAll() {
        users.clear()
        nextId = 1L
    }
}