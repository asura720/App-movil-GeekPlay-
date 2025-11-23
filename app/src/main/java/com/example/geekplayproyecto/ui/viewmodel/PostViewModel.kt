package com.example.geekplayproyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geekplayproyecto.data.repository.PostRepository
import com.example.geekplayproyecto.domain.geekplay.Category
import com.example.geekplayproyecto.domain.geekplay.Post
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class PostViewModel(
    private val repo: PostRepository,
    private val filterEmail: String? = null
) : ViewModel() {

    private val category = MutableStateFlow<Category?>(null)
    private val searchQuery = MutableStateFlow("")

    // ✅ NUEVO: Un contador simple para forzar la recarga
    private val refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts: StateFlow<List<Post>> =
        // ✅ MODIFICADO: Agregamos refreshTrigger al combine
        combine(category, searchQuery, refreshTrigger) { cat, search, _ ->
            Triple(cat, search, Unit) // Usamos Triple para pasar los datos
        }.flatMapLatest { (cat, search, _) ->
            when {
                search.isNotBlank() -> repo.search(search)
                filterEmail != null -> repo.getByAuthorEmail(filterEmail)
                cat == null -> repo.getAll()
                else -> repo.getByCategory(cat.name)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun setCategory(c: Category?) {
        category.value = c
        searchQuery.value = ""
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
        if (query.isNotBlank()) {
            category.value = null
        }
    }

    // ✅ NUEVA FUNCIÓN: Llama a esto para recargar la lista
    fun refresh() {
        refreshTrigger.value += 1
    }

    @Suppress("unused")
    suspend fun getById(id: String): Post? = repo.get(id)
}