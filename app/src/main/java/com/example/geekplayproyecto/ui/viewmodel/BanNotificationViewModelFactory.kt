package com.example.geekplayproyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.geekplayproyecto.data.local.ban.BanNotificationRepository

class BanNotificationViewModelFactory(
    private val repository: BanNotificationRepository,
    private val userId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BanNotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BanNotificationViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
