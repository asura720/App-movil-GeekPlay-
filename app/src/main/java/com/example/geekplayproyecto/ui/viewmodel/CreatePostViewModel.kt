package com.example.geekplayproyecto.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geekplayproyecto.data.repository.PostRepository
import com.example.geekplayproyecto.data.repository.UserRepository
import com.example.geekplayproyecto.domain.geekplay.Category
import com.example.geekplayproyecto.domain.geekplay.Post
import com.example.geekplayproyecto.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreatePostUiState(
    val title: String = "",
    val summary: String = "",
    val content: String = "",
    val category: Category = Category.PELICULAS,
    val imageUri: String? = null,
    val isSubmitting: Boolean = false,
    val errorMsg: String? = null,
    val success: Boolean = false
)

class CreatePostViewModel(
    private val repo: PostRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(CreatePostUiState())
    val ui: StateFlow<CreatePostUiState> = _ui

    fun onTitleChange(v: String) = _ui.update { it.copy(title = v) }
    fun onSummaryChange(v: String) = _ui.update { it.copy(summary = v) }
    fun onContentChange(v: String) = _ui.update { it.copy(content = v) }
    fun onCategoryChange(c: Category) = _ui.update { it.copy(category = c) }
    fun onImageSelected(uri: Uri?) = _ui.update { it.copy(imageUri = uri?.toString()) }

    // ✅ CAMBIO: Ahora recibe authorId y authorName directamente
    fun create(authorId: Long, authorName: String, authorEmail: String, context: Context) {
        val s = _ui.value
        if (s.title.isBlank() || s.summary.isBlank() || s.content.isBlank()) {
            _ui.update { it.copy(errorMsg = "Completa todos los campos") }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            // ❌ Eliminamos la búsqueda por email que fallaba

            val savedImagePath = s.imageUri?.let {
                ImageUtils.saveImageToInternalStorage(context, it.toUri())
            }

            val post = Post(
                id = "",
                authorId = authorId, // ✅ Usamos el ID recibido
                title = s.title.trim(),
                summary = s.summary.trim(),
                content = s.content.trim(),
                category = s.category,
                authorName = authorName,
                publishedAt = System.currentTimeMillis(),
                likes = 0,
                imageUrl = savedImagePath
            )

            // Enviamos el email solo si el repositorio antiguo lo pide, pero el ID es lo que importa para la DB
            runCatching { repo.create(post, authorEmail) }
                .onSuccess { _ ->
                    _ui.update { state -> state.copy(isSubmitting = false, success = true) }
                }
                .onFailure { e ->
                    _ui.update { state ->
                        state.copy(isSubmitting = false, errorMsg = e.message ?: "Error al crear el post")
                    }
                }
        }
    }

    fun reset() { _ui.value = CreatePostUiState() }
}