package com.example.geekplayproyecto.data.repository

import com.example.geekplayproyecto.data.local.user.UserDao
import com.example.geekplayproyecto.data.local.user.UserEntity
import com.example.geekplayproyecto.utils.PasswordUtils

/**
 * Repositorio de Usuarios sobre Room (SQLite).
 * - Login con validación de hash de contraseña.
 * - Registro con hash de contraseña y control de duplicados.
 */
class UserRepository(
    private val userDao: UserDao
) {

    /**
     * Login: valida credenciales contra la base local.
     * Busca el usuario por email y verifica el hash de la contraseña.
     */
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.findByEmail(email)
        return if (user != null && PasswordUtils.verifyPassword(password, user.password)) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    /**
     * Registro: inserta un usuario nuevo con contraseña hasheada.
     * - Usa INSERT IGNORE (en DAO) + índice único en email.
     * - Si Room devuelve -1L, el correo ya existe.
     */
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<Long> {
        val hashedPassword = PasswordUtils.hashPassword(password)

        val id = userDao.insert(
            UserEntity(
                name = name,
                email = email,
                phone = phone,
                password = hashedPassword
            )
        )
        return if (id == -1L) {
            Result.failure(IllegalStateException("El correo ya está registrado"))
        } else {
            Result.success(id)
        }
    }

    // ---------- Utilidades opcionales (útiles para perfil/edición) ----------

    /** Obtiene un usuario por email (útil para prefijar datos de perfil). */
    suspend fun findByEmail(email: String): UserEntity? =
        userDao.findByEmail(email)

    /** Actualiza los datos del usuario. */
    suspend fun update(user: UserEntity) =
        userDao.update(user)
}