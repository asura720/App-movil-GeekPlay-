package com.example.geekplayproyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.geekplayproyecto.data.repository.PostRepository

class PostViewModelFactory(
    private val repo: PostRepository,
    //  1. AÑADIDO: Recibe el email de filtro. Es opcional y puede ser nulo.
    private val filterEmail: String? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            //  2. PASA EL ARGUMENTO al PostViewModel.
            return PostViewModel(
                repo = repo,
                filterEmail = filterEmail
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}