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

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts: StateFlow<List<Post>> =
        combine(category, searchQuery) { cat, search ->
            Pair(cat, search)
        }.flatMapLatest { (cat, search) ->
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

    @Suppress("unused")
    suspend fun getById(id: String): Post? = repo.get(id)
}