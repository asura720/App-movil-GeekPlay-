package com.example.geekplayproyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geekplayproyecto.data.local.ban.BanNotification
import com.example.geekplayproyecto.data.local.ban.BanNotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class BanNotificationViewModel(banNotificationRepository: BanNotificationRepository, userId: Long) : ViewModel() {

    val banNotifications: StateFlow<List<BanNotification>> = banNotificationRepository.getBanNotificationsByUser(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

}
