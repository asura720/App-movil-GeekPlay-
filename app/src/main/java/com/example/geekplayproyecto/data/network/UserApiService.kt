package com.example.geekplayproyecto.data.network

import com.example.geekplayproyecto.data.local.user.UserEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// --- DTOs (Datos que enviamos al servidor) ---

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)

// ✅ DTO para actualizar perfil (Nivel superior)
data class UpdateProfileRequest(
    val name: String,
    val phone: String,
    val profileImagePath: String?
)

// ✅ DTO para cambiar contraseña (Nivel superior)
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

// --- INTERFAZ DE LA API ---

interface UserApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<UserEntity>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserEntity>

    // Obtener usuario por ID
    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UserEntity>

    // ❌ AQUÍ BORRAMOS LAS CLASES DUPLICADAS QUE TENÍAS

    // Actualizar perfil
    @PUT("api/users/{id}")
    suspend fun updateProfile(
        @Path("id") id: Long,
        @Body request: UpdateProfileRequest
    ): Response<UserEntity>

    // Cambiar contraseña
    @PUT("api/users/{id}/password")
    suspend fun changePassword(
        @Path("id") id: Long,
        @Body request: ChangePasswordRequest
    ): Response<Void>
}