package com.example.geekplayproyecto.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

/**
 * Representa un usuario almacenado localmente en Room (SQLite).
 * Incluye índice único para evitar duplicar correos electrónicos.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)] // 🔒 evita emails duplicados
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,                 // Clave primaria autoincremental

    val name: String,                  // Nombre completo del usuario
    val email: String,                 // Correo electrónico (único)
    val phone: String,                 // Teléfono
    val password: String,              // Contraseña hasheada
    val profileImagePath: String? = null,  // Ruta de la foto de perfil
    val isAdmin: Boolean = false       // ⬅️ NUEVO: Indica si es administrador
)