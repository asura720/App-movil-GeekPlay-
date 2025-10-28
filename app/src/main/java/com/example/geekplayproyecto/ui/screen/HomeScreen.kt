package com.example.geekplayproyecto.ui.screen

import androidx.core.net.toUri
import androidx.compose.animation.* 
import androidx.compose.animation.core.tween 
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.geekplayproyecto.data.local.storage.UserPreferences
import com.example.geekplayproyecto.domain.geekplay.Category
import com.example.geekplayproyecto.ui.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: PostViewModel,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onCreate: () -> Unit,
    onOpenDetail: (String) -> Unit
) {
    val context = LocalContext.current

    val userPrefs = remember { UserPreferences(context) }
    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(false)

    var selectedCategory by rememberSaveable { mutableStateOf<Category?>(null) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var expandedImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedCategory) { vm.setCategory(selectedCategory) }
    LaunchedEffect(searchQuery) { vm.setSearchQuery(searchQuery) }

    val posts by vm.posts.collectAsStateWithLifecycle(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Gamepad,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "GeekPlay",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSearchDialog = true }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            if (isLoggedIn) {
                ExtendedFloatingActionButton(
                    onClick = onCreate,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nuevo Post") },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                )
            }
        }
    ) { padd ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padd),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    UserStatusCard(
                        isLoggedIn = isLoggedIn,
                        onGoLogin = onGoLogin,
                        onGoRegister = onGoRegister
                    )
                }

                if (searchQuery.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Buscando: \"$searchQuery\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, "Limpiar búsqueda")
                                }
                            }
                        }
                    }
                }

                if (searchQuery.isEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                "Explorar categorías",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.height(12.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    CategoryChip(
                                        label = "Todos",
                                        icon = Icons.Default.GridView,
                                        selected = selectedCategory == null,
                                        onClick = { selectedCategory = null }
                                    )
                                }
                                items(Category.entries.toList()) { cat ->
                                    CategoryChip(
                                        label = cat.name.lowercase().replaceFirstChar { it.uppercase() },
                                        icon = getCategoryIcon(cat),
                                        selected = selectedCategory == cat,
                                        onClick = { selectedCategory = cat }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    val targetContentState = posts.isEmpty()

                    AnimatedContent(
                        targetState = targetContentState,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(400)) + scaleIn(
                                animationSpec = tween(400),
                                initialScale = 0.95f
                            )) togetherWith
                                    (fadeOut(animationSpec = tween(400)) + scaleOut(
                                        animationSpec = tween(400),
                                        targetScale = 0.95f
                                    ))
                        },
                        label = "PostListTransition",
                        modifier = Modifier.fillMaxWidth()
                    ) { isEmptyState ->
                        if (isEmptyState) {
                            EmptyStateCard(
                                title = if (searchQuery.isNotEmpty()) "No se encontraron resultados" else "No hay publicaciones",
                                subtitle = if (searchQuery.isNotEmpty()) "Intenta con otra búsqueda" else if (isLoggedIn) "¡Sé el primero en crear una!" else "Inicia sesión para crear posts",
                                icon = if (searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.AutoMirrored.Filled.Article
                            )
                        } else {
                            Column {
                                Text(
                                    if (searchQuery.isNotEmpty()) "Resultados (${posts.size})" else "Últimas publicaciones",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                posts.forEach { post ->
                                    ProfessionalPostCard(
                                        post = post,
                                        onClick = { onOpenDetail(post.id) },
                                        onImageClick = { imageUrl -> expandedImageUrl = imageUrl },
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }

            if (expandedImageUrl != null) {
                ImageFullScreenDialog(
                    imageUrl = expandedImageUrl!!,
                    onDismiss = { expandedImageUrl = null }
                )
            }
        }
    }

    if (showSearchDialog) {
        SearchDialog(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onDismiss = {
                showSearchDialog = false
            }
        )
    }
}

@Composable
private fun CategoryChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = FilterChipDefaults.filterChipElevation(
            elevation = if (selected) 4.dp else 2.dp
        )
    )
}

@Composable
private fun ProfessionalPostCard(
    post: com.example.geekplayproyecto.domain.geekplay.Post,
    onClick: () -> Unit,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp, pressedElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable(
                        enabled = post.imageUrl != null,
                        onClick = {
                            post.imageUrl?.let { onImageClick(it) }
                        }
                    )
            ) {
                if (post.imageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(com.example.geekplayproyecto.utils.ImageUtils.getFileUri(post.imageUrl))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen del post",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    )

                    Icon(
                        Icons.Default.ZoomIn,
                        contentDescription = "Ver imagen completa",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(24.dp),
                        tint = Color.White.copy(alpha = 0.9f)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                    )
                                )
                            )
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                post.category.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                getCategoryIcon(post.category),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color.White.copy(alpha = 0.95f),
                            labelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = null
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    post.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    post.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(12.dp))

                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (post.authorProfileImageUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(com.example.geekplayproyecto.utils.ImageUtils.getFileUri(post.authorProfileImageUrl))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto de perfil del autor",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    post.authorName.firstOrNull()?.toString() ?: "?",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                post.authorName,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "Hace ${getTimeAgo(post.publishedAt)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Likes",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${post.likes}",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ImageFullScreenDialog(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(
                        if (imageUrl.startsWith("content://")) {
                            imageUrl.toUri()
                        } else {
                            com.example.geekplayproyecto.utils.ImageUtils.getFileUri(imageUrl)
                        }
                    )
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen expandida",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Toca en cualquier lugar para cerrar",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun UserStatusCard(
    isLoggedIn: Boolean,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoggedIn)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isLoggedIn) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isLoggedIn)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        if (isLoggedIn) "Sesión activa" else "No has iniciado sesión",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isLoggedIn)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        if (isLoggedIn) "Disfruta de todas las funciones" else "Accede a contenido exclusivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isLoggedIn)
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        else
                            MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    )
                }
            }
            if (!isLoggedIn) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilledTonalButton(
                        onClick = onGoLogin,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                            contentColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.widthIn(min = 90.dp)
                    ) {
                        Text("Entrar", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = onGoRegister,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.widthIn(min = 90.dp)
                    ) {
                        Text("Registro", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchDialog(
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Buscar posts",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe algo...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                if (query.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Busca en títulos, resúmenes y contenido",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        dismissButton = {
            if (query.isNotEmpty()) {
                TextButton(onClick = { onQueryChange("") }) {
                    Text("Limpiar")
                }
            }
        }
    )
}

private fun getCategoryIcon(category: Category): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        Category.VIDEOJUEGOS -> Icons.Default.SportsEsports
        Category.PELICULAS -> Icons.Default.Movie
        Category.SERIES -> Icons.Default.Tv
        Category.COMICS -> Icons.AutoMirrored.Filled.MenuBook
    }
}

private fun getTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / 60000
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "un momento"
        minutes < 60 -> "$minutes min"
        hours < 24 -> "$hours h"
        days < 7 -> "$days días"
        else -> "${days / 7} semanas"
    }
}