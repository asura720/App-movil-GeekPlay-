package com.example.geekplayproyecto.data.repository

import com.example.geekplayproyecto.data.local.user.UserEntity
import com.example.geekplayproyecto.data.network.*
import com.example.geekplayproyecto.data.network.RetrofitClient
import com.example.geekplayproyecto.data.network.UpdateProfileRequest
import com.example.geekplayproyecto.data.network.ChangePasswordRequest

class UserRepository {

    private val api = RetrofitClient.userApi

    suspend fun login(email: String, password: String): Result<UserEntity> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, phone: String, pass: String): Result<Long> {
        return try {
            val response = api.register(RegisterRequest(name, email, phone, pass))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.id)
            } else {
                Result.failure(Exception("Error al registrar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ NUEVO: Obtener por ID
    suspend fun getById(id: Long): UserEntity? {
        return try {
            val response = api.getUserById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                // Log para depurar
                println("Error API User: ${response.code()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ✅ NUEVO: Actualizar Perfil
    suspend fun updateProfile(userId: Long, name: String, phone: String, imagePath: String?): Result<UserEntity> {
        return try {
            val request = UpdateProfileRequest(name, phone, imagePath)
            val response = api.updateProfile(userId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //  NUEVO: Cambiar Contraseña
    suspend fun changePassword(userId: Long, current: String, newPass: String): Result<String> {
        return try {
            val request = ChangePasswordRequest(current, newPass)
            val response = api.changePassword(userId, request)
            if (response.isSuccessful) {
                Result.success("Contraseña actualizada")
            } else {
                Result.failure(Exception("Error: Verifique su contraseña actual"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    }
