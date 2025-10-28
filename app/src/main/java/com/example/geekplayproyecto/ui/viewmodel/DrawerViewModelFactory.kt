package com.example.geekplayproyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.geekplayproyecto.data.local.storage.UserPreferences

class DrawerViewModelFactory(private val userPrefs: UserPreferences) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrawerViewModel::class.java)) {
            return DrawerViewModel(userPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}