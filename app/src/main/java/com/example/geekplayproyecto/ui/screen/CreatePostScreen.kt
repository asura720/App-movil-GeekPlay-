package com.example.geekplayproyecto.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.geekplayproyecto.data.local.storage.UserPreferences
import com.example.geekplayproyecto.data.local.user.UserEntity
import com.example.geekplayproyecto.domain.geekplay.Category
import com.example.geekplayproyecto.ui.viewmodel.CreatePostViewModel
import com.example.geekplayproyecto.ui.viewmodel.CreatePostViewModelFactory
import com.example.geekplayproyecto.utils.ServiceLocator
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreatePostScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val postRepository = ServiceLocator.providePostRepository(context)
    val userRepository = ServiceLocator.provideUserRepository(context)

    val vm: CreatePostViewModel = viewModel(
        factory = CreatePostViewModelFactory(postRepository, userRepository)
    )

    val userPrefs = remember { UserPreferences(context) }

    // ✅ CAMBIO: Obtenemos el userId de las preferencias
    val userId by userPrefs.userId.collectAsStateWithLifecycle(-1L)

    var user by remember { mutableStateOf<UserEntity?>(null) }

    // ✅ CAMBIO: Buscamos al usuario por ID, no por email
    LaunchedEffect(userId) {
        if (userId != -1L) {
            user = userRepository.getById(userId)
        }
    }

    val ui by vm.ui.collectAsStateWithLifecycle()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        vm.onImageSelected(uri)
    }

    LaunchedEffect(ui.success) {
        if (ui.success) {
            vm.reset()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Post") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, "Cancelar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padd ->
        val currentUser = user
        if (currentUser != null && currentUser.bannedUntil != null && currentUser.bannedUntil > System.currentTimeMillis()) {
            BannedScreen(bannedUntil = currentUser.bannedUntil)
        } else {
            Column(
                modifier = Modifier
                    .padding(padd)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Publicando como: ${currentUser?.name ?: "Cargando..."}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = ui.title,
                    onValueChange = vm::onTitleChange,
                    label = { Text("Título") },
                    placeholder = { Text("Escribe un título llamativo...") },
                    modifier = Modifier.fillMaxWidth()
                        .testTag("txtTitulo"),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = ui.summary,
                    onValueChange = vm::onSummaryChange,
                    label = { Text("Resumen") },
                    placeholder = { Text("Breve descripción del post...") },
                    modifier = Modifier.fillMaxWidth()
                        .testTag("txtResumen"),
                    minLines = 2,
                    maxLines = 3
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = ui.content,
                    onValueChange = vm::onContentChange,
                    label = { Text("Contenido") },
                    placeholder = { Text("Escribe el contenido completo...") },
                    modifier = Modifier.fillMaxWidth()
                        .testTag("txtContenido"),
                    minLines = 8
                )
                Spacer(Modifier.height(16.dp))

                Text(
                    "Categoría",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Category.entries.forEach { c ->
                        FilterChip(
                            selected = c == ui.category,
                            onClick = { vm.onCategoryChange(c) },
                            label = { Text(c.name) }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Imagen (opcional)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(8.dp))

                val imageUri = ui.imageUri
                if (imageUri != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageUri.toUri())
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { vm.onImageSelected(null) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.error,
                                        RoundedCornerShape(50)
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Quitar imagen",
                                    tint = MaterialTheme.colorScheme.onError
                                )
                            }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                            .testTag("btnSeleccionarImagen")
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Seleccionar imagen")
                    }
                }

                if (ui.errorMsg != null) {
                    Spacer(Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            ui.errorMsg ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        enabled = !ui.isSubmitting,
                        modifier = Modifier.weight(1f)
                            .testTag("btnCancelar")
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if (currentUser != null) {
                                // CAMBIO: Pasamos el ID y los datos al ViewModel
                                vm.create(
                                    authorId = currentUser.id,
                                    authorName = currentUser.name,
                                    authorEmail = currentUser.email,
                                    context = context
                                )
                            }
                        },
                        enabled = !ui.isSubmitting && currentUser != null,
                        modifier = Modifier.weight(1f)
                            .testTag("btnPublicar")
                    ) {
                        if (ui.isSubmitting) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Publicando...")
                        } else {
                            Text("Publicar")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun BannedScreen(bannedUntil: Long) {
    var remainingTime by remember { mutableLongStateOf(bannedUntil - System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (remainingTime > 0) {
            delay(1000)
            remainingTime = bannedUntil - System.currentTimeMillis()
        }
    }

    val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Estás suspendido", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            Text("No puedes crear publicaciones hasta que termine tu suspensión.")
            Spacer(Modifier.height(16.dp))
            Text("Tiempo restante: ${minutes}m ${seconds}s")
        }
    }
}