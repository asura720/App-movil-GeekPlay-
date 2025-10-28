package com.example.geekplayproyecto.data.local.user

import androidx.room.*

@Dao
interface UserDao {

    //  Inserta un usuario nuevo; ignora si el email ya existe
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity): Long

    // Actualiza los datos de un usuario (nombre, teléfono, contraseña, etc.)
    @Update
    suspend fun update(user: UserEntity)

    //  Elimina un usuario
    @Delete
    suspend fun delete(user: UserEntity)

    // 🔹 Obtiene todos los usuarios (por si necesitas mostrar lista)
    @Query("SELECT * FROM users ORDER BY name ASC")
    suspend fun getAll(): List<UserEntity>

    //  Busca un usuario por su correo (usado para login y perfil)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    //  Cuenta total de usuarios
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

}