package com.example.geekplayproyecto.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geekplayproyecto.data.local.user.UserEntity
import com.example.geekplayproyecto.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    fun loadUser(userId: Long) {
        Log.d("SessionViewModel", "Cargando usuario con ID: $userId")
        viewModelScope.launch {
            try {
                val user = userRepository.getById(userId)
                if (user != null) {
                    Log.d("SessionViewModel", "Usuario cargado con éxito: ${user.name}")
                    _currentUser.value = user
                } else {
                    Log.e("SessionViewModel", "Usuario es NULL. El repositorio no devolvió nada.")
                    _currentUser.value = null
                }
            } catch (e: Exception) {
                Log.e("SessionViewModel", "Error al cargar usuario: ${e.message}")
                e.printStackTrace()
                _currentUser.value = null
            }
        }
    }

    fun updateUser(user: UserEntity) {
        _currentUser.value = user
    }

    fun clearSession() {
        _currentUser.value = null
    }
}