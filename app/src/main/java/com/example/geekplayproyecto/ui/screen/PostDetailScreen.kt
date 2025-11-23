package com.example.geekplayproyecto.ui.screen

import android.content.Intent
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.geekplayproyecto.data.local.comment.Comment
import com.example.geekplayproyecto.data.local.like.Like
import com.example.geekplayproyecto.data.local.storage.UserPreferences
import com.example.geekplayproyecto.ui.viewmodel.PostDetailViewModel
import com.example.geekplayproyecto.utils.ImageUtils
import com.example.geekplayproyecto.utils.ImageMapper
import com.example.geekplayproyecto.utils.ServiceLocator
import java.text.SimpleDateFormat
import java.util.*

// Estado del diálogo: Guarda si es acción de admin (moderación) o usuario (borrado simple)
private sealed class DialogState {
    object NoDialog : DialogState()
    data class DeletePost(val isAdminAction: Boolean) : DialogState()
    data class DeleteComment(val commentId: String, val isAdminAction: Boolean) : DialogState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val isLoggedIn by userPrefs.isLoggedIn.collectAsState(initial = false)
    val currentUserId by userPrefs.userId.collectAsState(initial = -1L)
    val lastEmail by userPrefs.lastEmail.collectAsState(initial = null)

    val (dialogState, setDialogState) = remember { mutableStateOf<DialogState>(DialogState.NoDialog) }

    val vm: PostDetailViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val postRepo = ServiceLocator.providePostRepository(context)
                val commentRepo = ServiceLocator.provideCommentRepository(context)
                val likeRepo = ServiceLocator.provideLikeRepository(context)
                val userRepo = ServiceLocator.provideUserRepository(context)
                val banNotificationRepo = ServiceLocator.provideBanNotificationRepository(context)
                @Suppress("UNCHECKED_CAST")
                return PostDetailViewModel(
                    postId,
                    postRepo,
                    commentRepo,
                    likeRepo,
                    userRepo,
                    banNotificationRepo
                ) as T
            }
        }
    )

    val ui by vm.ui.collectAsStateWithLifecycle()

    LaunchedEffect(lastEmail) {
        lastEmail?.let { vm.init(it) }
    }

    // --- LÓGICA DE DIÁLOGOS ---
    when (dialogState) {
        is DialogState.DeletePost -> {
            if (dialogState.isAdminAction) {
                // Admin moderando: Pide motivo y baneo
                DeleteConfirmationDialog(
                    onDismiss = { setDialogState(DialogState.NoDialog) },
                    onConfirm = { reason, duration ->
                        vm.deletePostWithReason(reason, duration, onBack)
                        setDialogState(DialogState.NoDialog)
                    }
                )
            } else {
                // Usuario borrando lo suyo: Confirmación simple
                SimpleDeleteDialog(
                    title = "Eliminar Publicación",
                    text = "¿Estás seguro de que quieres eliminar esta publicación?",
                    onDismiss = { setDialogState(DialogState.NoDialog) },
                    onConfirm = {
                        vm.deletePostWithReason("Borrado por el usuario", 0, onBack)
                        setDialogState(DialogState.NoDialog)
                    }
                )
            }
        }
        is DialogState.DeleteComment -> {
            if (dialogState.isAdminAction) {
                // Admin moderando
                DeleteConfirmationDialog(
                    onDismiss = { setDialogState(DialogState.NoDialog) },
                    onConfirm = { reason, duration ->
                        vm.deleteCommentWithReason(dialogState.commentId, reason, duration)
                        setDialogState(DialogState.NoDialog)
                    }
                )
            } else {
                // Usuario borrando lo suyo
                SimpleDeleteDialog(
                    title = "Eliminar Comentario",
                    text = "¿Quieres borrar este comentario?",
                    onDismiss = { setDialogState(DialogState.NoDialog) },
                    onConfirm = {
                        vm.deleteCommentWithReason(dialogState.commentId, "Borrado por el usuario", 0)
                        setDialogState(DialogState.NoDialog)
                    }
                )
            }
        }
        is DialogState.NoDialog -> {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ui.post?.title ?: "...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Verifica si es el autor del post
                    val isAuthor = (ui.post?.authorId == currentUserId)

                    // Muestra borrar si es Admin O es el Autor
                    if (ui.isCurrentUserAdmin || isAuthor) {
                        IconButton(onClick = {
                            // Es acción de Admin SOLO si NO es el autor (está moderando a otro)
                            val isAdminAction = ui.isCurrentUserAdmin && !isAuthor
                            setDialogState(DialogState.DeletePost(isAdminAction))
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar post", tint = Color.White)
                        }
                    }

                    IconButton(onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "¡Mira este post de GeekPlay!\n\n${ui.post?.title}\n\n${ui.post?.summary}")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    },
                        modifier = Modifier.testTag("btnShare")) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir", tint = Color.White)
                    }

                    if(isLoggedIn) {
                        IconToggleButton(checked = ui.hasUserLiked, onCheckedChange = { lastEmail?.let { vm.toggleLike(it) } },
                            modifier = Modifier.testTag("btnLike")) {
                            Icon(
                                imageVector = if (ui.hasUserLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Me gusta",
                                tint = if (ui.hasUserLiked) Color.Red else Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                if(ui.isLoadingPost) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    ui.post?.let {
                        PostContent(post = it)
                    }
                }
            }

            item {
                LikesSection(likes = ui.likes)
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Comentarios (${ui.comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .testTag("txtComentarios")
                )
                Spacer(Modifier.height(8.dp))
            }

            if (ui.isLoadingComments) {
                item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
            } else {
                items(ui.comments) { comment ->
                    val isAuthor = (currentUserId == comment.authorId)
                    val canDelete = isAuthor || ui.isCurrentUserAdmin

                    CommentCard(
                        comment = comment,
                        canDelete = canDelete,
                        onDelete = {
                            val isAdminAction = ui.isCurrentUserAdmin && !isAuthor
                            setDialogState(DialogState.DeleteComment(comment.id, isAdminAction))
                        }
                    )
                }
            }

            item {
                if(isLoggedIn) {
                    CommentInput(
                        commentText = ui.commentText,
                        onCommentChange = { vm.onCommentTextChange(it) },
                        onSend = {
                            if (currentUserId != -1L) {
                                vm.addComment(currentUserId)
                            }
                        }
                    )
                }
            }
        }
    }
}

// Diálogo Completo (Moderación)
@Composable
private fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var banDuration by remember { mutableIntStateOf(0) }
    val banOptions = listOf("No suspender" to 0, "5 minutos" to 5, "10 minutos" to 10, "20 minutos" to 20, "1 hora" to 60)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Eliminación (Moderación)") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Por favor, introduce el motivo de la eliminación.")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Motivo") }
                )
                Spacer(Modifier.height(16.dp))
                Text("Selecciona una duración para la suspensión:")
                banOptions.forEach { (text, duration) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = banDuration == duration,
                            onClick = { banDuration = duration }
                        )
                        Text(text)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(reason, banDuration) },
                enabled = reason.isNotBlank()
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Diálogo Simple (Usuario)
@Composable
private fun SimpleDeleteDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun PostContent(post: com.example.geekplayproyecto.domain.geekplay.Post) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        if (post.imageUrl != null) {
            val resourceId = ImageMapper.getDrawableIdFromName(post.imageUrl)

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(
                        when {
                            resourceId != null -> resourceId
                            post.imageUrl.startsWith("content://") -> post.imageUrl.toUri()
                            else -> ImageUtils.getFileUri(post.imageUrl)
                        }
                    )
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen del post",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(16.dp))
        }

        Text(post.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(post.summary, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        Text(post.content, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun LikesSection(likes: List<Like>) {
    if (likes.isNotEmpty()) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
            Text("Le ha gustado a:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(likes) { like ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val imagePath = like.userProfileImageUrl
                        val resourceId = ImageMapper.getDrawableIdFromName(imagePath)

                        if (imagePath != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(
                                        when {
                                            resourceId != null -> resourceId
                                            imagePath.startsWith("content://") -> imagePath.toUri()
                                            else -> ImageUtils.getFileUri(imagePath)
                                        }
                                    )
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(like.userName.firstOrNull()?.toString() ?: "?", color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                        Text(like.userName, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentCard(comment: Comment, canDelete: Boolean, onDelete: () -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            val imagePath = comment.authorProfileImageUrl
            val resourceId = ImageMapper.getDrawableIdFromName(imagePath)

            if (imagePath != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(
                            when {
                                resourceId != null -> resourceId
                                imagePath.startsWith("content://") -> imagePath.toUri()
                                else -> ImageUtils.getFileUri(imagePath)
                            }
                        )
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de perfil del autor",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        comment.authorName.firstOrNull()?.toString()?.uppercase() ?: "?",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(comment.authorName, fontWeight = FontWeight.Bold)
                val dateString = try {
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(comment.timestamp))
                } catch (e: Exception) {
                    "Hace un momento"
                }
                Text(
                    dateString,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(Modifier.height(4.dp))
                Text(comment.content)
            }

            if (canDelete) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar comentario", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentInput(
    commentText: String,
    onCommentChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = commentText,
            onValueChange = onCommentChange,
            modifier = Modifier.weight(1f)
                .testTag("txtComentario"),
            placeholder = { Text("Escribe un comentario...") },
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onSend, enabled = commentText.isNotBlank(),
            modifier = Modifier.testTag("btnEnviarComentario")) {
            Icon(Icons.AutoMirrored.Filled.Send, "Enviar", tint = MaterialTheme.colorScheme.primary)
        }
    }
}