package com.example.geekplayproyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geekplayproyecto.data.local.ban.BanNotification
import com.example.geekplayproyecto.data.local.ban.BanNotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BanNotificationViewModel(
    private val repository: BanNotificationRepository,
    private val userId: Long
) : ViewModel() {

    // Cambiamos a MutableStateFlow para poder actualizar manualmente
    private val _banNotifications = MutableStateFlow<List<BanNotification>>(emptyList())
    val banNotifications: StateFlow<List<BanNotification>> = _banNotifications.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            repository.getBanNotificationsByUser(userId).collect {
                _banNotifications.value = it
            }
        }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            repository.delete(notificationId)
            loadNotifications() // 🔄 Recargar la lista después de borrar
        }
    }
}