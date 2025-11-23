package com.example.geekplayproyecto.data.local.user

// ✅ Ya no es una tabla de Room, es un modelo de datos simple
data class UserEntity(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String,
    val profileImagePath: String? = null,
    val isAdmin: Boolean = false,
    val bannedUntil: Long? = null
    // Nota: No incluimos 'password' porque el servidor no la devuelve por seguridad
)