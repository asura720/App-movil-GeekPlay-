package com.example.geekplayproyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geekplayproyecto.data.local.ban.BanNotificationRepository
import com.example.geekplayproyecto.data.local.comment.Comment
import com.example.geekplayproyecto.data.local.like.Like
import com.example.geekplayproyecto.data.repository.CommentRepository
import com.example.geekplayproyecto.data.repository.LikeRepository
import com.example.geekplayproyecto.data.repository.PostRepository
import com.example.geekplayproyecto.data.repository.UserRepository
import com.example.geekplayproyecto.domain.geekplay.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostDetailUiState(
    val isLoadingPost: Boolean = true,
    val isLoadingComments: Boolean = true,
    val post: Post? = null,
    val comments: List<Comment> = emptyList(),
    val commentText: String = "",
    val likes: List<Like> = emptyList(),
    val hasUserLiked: Boolean = false,
    val isCurrentUserAdmin: Boolean = false
)

class PostDetailViewModel(
    private val postId: String,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val likeRepository: LikeRepository,
    private val userRepository: UserRepository,
    private val banNotificationRepository: BanNotificationRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(PostDetailUiState())
    val ui: StateFlow<PostDetailUiState> = _ui.asStateFlow()

    fun init(userEmail: String) {
        // En una app real, verificaríamos el rol con el token o una llamada a /me
        // Por simplicidad en la demo, asumimos admin por email
        if (userEmail == "admin@geekplay.cl") {
            _ui.update { it.copy(isCurrentUserAdmin = true) }
        }

        loadPost()
        loadComments()
        observeLikes(userEmail)
    }

    private fun loadPost() {
        viewModelScope.launch {
            _ui.update { it.copy(isLoadingPost = true) }
            val post = postRepository.get(postId)
            _ui.update { it.copy(post = post, isLoadingPost = false) }
        }
    }

    private fun loadComments() {
        viewModelScope.launch {
            commentRepository.observeByPost(postId).collect { comments ->
                _ui.update { it.copy(comments = comments, isLoadingComments = false) }
            }
        }
    }

    private fun observeLikes(userEmail: String) {
        viewModelScope.launch {
            likeRepository.getLikesForPost(postId).collect { likes ->
                val hasLiked = likes.any { it.userEmail == userEmail }
                _ui.update { it.copy(likes = likes, hasUserLiked = hasLiked) }
            }
        }
    }

    fun onCommentTextChange(text: String) {
        _ui.update { it.copy(commentText = text) }
    }

    fun addComment(authorId: Long) {
        viewModelScope.launch {
            val content = _ui.value.commentText
            if (content.isNotBlank()) {
                commentRepository.addComment(postId, authorId, content)
                _ui.update { it.copy(commentText = "") }
                loadComments() // Recargar para ver el nuevo comentario
            }
        }
    }

    // 🗑️ ELIMINAR COMENTARIO (Delegado al Microservicio)
// En PostDetailViewModel.kt

    fun deleteCommentWithReason(commentId: String, reason: String, banDurationMinutes: Int) {
        viewModelScope.launch {
            val comment = _ui.value.comments.find { it.id == commentId }
            comment?.let {

                // Si el tiempo es 0, asumimos que es un borrado simple (propio o sin castigo)
                if (banDurationMinutes == 0) {
                    // 1. Borrado Directo (Más rápido y sin notificación de baneo)
                    commentRepository.deleteComment(commentId)
                } else {
                    // 2. Borrado con Moderación (Solo admins castigando)
                    banNotificationRepository.performModeration(
                        targetUserId = it.authorId,
                        contentId = commentId,
                        type = "COMMENT",
                        reason = reason,
                        durationMinutes = banDurationMinutes
                    )
                }

                // Actualizamos la lista localmente para que desaparezca instantáneamente
                val updatedComments = _ui.value.comments.filter { it.id != commentId }
                _ui.update { state -> state.copy(comments = updatedComments) }

                // Recargamos del servidor por si acaso
                loadComments()
            }
        }
    }

    fun toggleLike(userEmail: String) {
        viewModelScope.launch {
            likeRepository.toggleLike(postId, userEmail)
            observeLikes(userEmail)
        }
    }

    // 🗑️ ELIMINAR POST (Delegado al Microservicio)
    fun deletePostWithReason(reason: String, banDurationMinutes: Int, onPostDeleted: () -> Unit) {
        viewModelScope.launch {
            _ui.value.post?.let { post ->
                // Llamamos a la API de moderación
                banNotificationRepository.performModeration(
                    targetUserId = post.authorId,
                    contentId = postId,
                    type = "POST",
                    reason = reason,
                    durationMinutes = banDurationMinutes
                )

                // Navegamos atrás
                onPostDeleted()
            }
        }
    }
}