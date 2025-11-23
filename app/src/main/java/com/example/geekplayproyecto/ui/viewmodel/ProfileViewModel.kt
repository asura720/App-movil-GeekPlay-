package com.example.geekplayproyecto.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geekplayproyecto.data.local.user.UserEntity
import com.example.geekplayproyecto.data.repository.UserRepository
import com.example.geekplayproyecto.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.net.toUri

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: UserEntity? = null,
    val error: String? = null,
    val successMsg: String? = null,
    val isEditing: Boolean = false,

    // Campos editables
    val editName: String = "",
    val editPhone: String = "",

    // Campos para contraseña (incluyendo la actual para validación)
    val currentPassword: String = "",
    val editPassword: String = "",
    val editConfirmPassword: String = "",

    val editProfileImageUri: String? = null
)

class ProfileViewModel(
    private val repo: UserRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ProfileUiState())
    val ui: StateFlow<ProfileUiState> = _ui

    fun loadUserById(userId: Long) {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null, successMsg = null) }
            val user = repo.getById(userId)

            if (user != null) {
                _ui.update { s ->
                    s.copy(
                        isLoading = false,
                        user = user,
                        editName = user.name,
                        editPhone = user.phone,
                        editProfileImageUri = user.profileImagePath
                    )
                }
            } else {
                _ui.update { it.copy(isLoading = false) }
            }
        }
    }

    fun startEditing() {
        _ui.update { it.copy(isEditing = true, error = null, successMsg = null) }
    }

    fun cancelEditing() {
        val user = _ui.value.user
        _ui.update {
            it.copy(
                isEditing = false,
                editName = user?.name ?: "",
                editPhone = user?.phone ?: "",
                currentPassword = "",
                editPassword = "",
                editConfirmPassword = "",
                editProfileImageUri = user?.profileImagePath,
                error = null
            )
        }
    }

    fun onNameChange(name: String) = _ui.update { it.copy(editName = name) }
    fun onPhoneChange(phone: String) = _ui.update { it.copy(editPhone = phone.filter { c -> c.isDigit() }) }

    // ✅ NUEVO: Capturar contraseña actual
    fun onCurrentPasswordChange(pass: String) = _ui.update { it.copy(currentPassword = pass) }

    fun onPasswordChange(password: String) = _ui.update { it.copy(editPassword = password) }
    fun onConfirmPasswordChange(confirm: String) = _ui.update { it.copy(editConfirmPassword = confirm) }

    fun onProfileImageSelected(uri: String?) = _ui.update { it.copy(editProfileImageUri = uri) }

    fun saveChanges(context: Context, onSaveSuccess: (UserEntity) -> Unit) {
        val state = _ui.value
        val user = state.user ?: return

        if (state.editName.isBlank()) {
            _ui.update { it.copy(error = "El nombre no puede estar vacío") }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null, successMsg = null) }

            // 1. Guardar imagen localmente si cambió
            val savedImagePath = if (state.editProfileImageUri != user.profileImagePath && state.editProfileImageUri != null) {
                if (state.editProfileImageUri.startsWith("content://")) {
                    ImageUtils.saveImageToInternalStorage(context, state.editProfileImageUri.toUri())
                } else {
                    state.editProfileImageUri
                }
            } else {
                user.profileImagePath
            }

            // 2. Actualizar Datos Básicos (Nombre, Teléfono, Foto)
            val updateResult = repo.updateProfile(
                userId = user.id,
                name = state.editName.trim(),
                phone = state.editPhone.trim(),
                imagePath = savedImagePath
            )

            if (updateResult.isFailure) {
                _ui.update { it.copy(isLoading = false, error = updateResult.exceptionOrNull()?.message) }
                return@launch
            }

            // Guardamos el usuario actualizado temporalmente
            var updatedUser = updateResult.getOrNull()!!

            // 3. ✅ LÓGICA DE CAMBIO DE CONTRASEÑA (Ahora sí se llama)
            if (state.editPassword.isNotBlank()) {
                // Validaciones locales
                if (state.currentPassword.isBlank()) {
                    _ui.update { it.copy(isLoading = false, error = "Ingresa tu contraseña actual para confirmar el cambio.") }
                    return@launch
                }
                if (state.editPassword.length < 8) {
                    _ui.update { it.copy(isLoading = false, error = "La nueva contraseña debe tener 8 caracteres.") }
                    return@launch
                }
                if (state.editPassword != state.editConfirmPassword) {
                    _ui.update { it.copy(isLoading = false, error = "Las contraseñas nuevas no coinciden.") }
                    return@launch
                }

                // Llamada al Repositorio -> API
                val passResult = repo.changePassword(user.id, state.currentPassword, state.editPassword)

                if (passResult.isFailure) {
                    // Si falla la contraseña, mostramos error y detenemos todo
                    _ui.update { it.copy(isLoading = false, error = passResult.exceptionOrNull()?.message ?: "Error al cambiar contraseña") }
                    return@launch
                }
            }

            // 4. Éxito Total
            _ui.update { s ->
                s.copy(
                    isLoading = false,
                    user = updatedUser,
                    isEditing = false,
                    currentPassword = "",
                    editPassword = "",
                    editConfirmPassword = "",
                    successMsg = "Perfil actualizado correctamente"
                )
            }
            onSaveSuccess(updatedUser)
        }
    }

    fun clearMessages() {
        _ui.update { it.copy(error = null, successMsg = null) }
    }
}