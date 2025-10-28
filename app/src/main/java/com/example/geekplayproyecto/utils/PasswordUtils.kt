package com.example.geekplayproyecto.utils

import java.security.MessageDigest

object PasswordUtils {

    /**
     * Hashea una contraseña usando SHA-256
     */
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    /**
     * Verifica si una contraseña coincide con su hash
     */
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return hashPassword(password) == hashedPassword
    }
}