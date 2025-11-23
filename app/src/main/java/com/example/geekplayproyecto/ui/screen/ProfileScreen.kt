package com.example.geekplayproyecto.ui.screen

import androidx.compose.ui.platform.testTag
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.geekplayproyecto.data.local.storage.UserPreferences
import com.example.geekplayproyecto.ui.viewmodel.ProfileViewModel
import com.example.geekplayproyecto.ui.viewmodel.SessionViewModel
import com.example.geekplayproyecto.utils.ImageUtils
import com.example.geekplayproyecto.utils.ImageMapper
import com.example.geekplayproyecto.utils.ServiceLocator
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    sessionViewModel: SessionViewModel
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val vm: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.provideUserRepository(context)
                return ProfileViewModel(repo) as T
            }
        }
    )

    val ui by vm.ui.collectAsStateWithLifecycle()
    val userId by userPrefs.userId.collectAsStateWithLifecycle(-1L)

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        vm.onProfileImageSelected(uri?.toString())
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            vm.onProfileImageSelected(tempImageUri?.toString())
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val newUri = ImageUtils.createTempImageUri(context)
            tempImageUri = newUri
            cameraLauncher.launch(newUri)
        }
    }

    LaunchedEffect(userId) {
        if (userId != -1L) {
            vm.loadUserById(userId)
        }
    }

    if (showImageSourceDialog) {
        ImageSourceDialog(
            onDismiss = { showImageSourceDialog = false },
            onGallery = {
                showImageSourceDialog = false
                imagePickerLauncher.launch("image/*")
            },
            onCamera = {
                showImageSourceDialog = false
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) -> {
                        val newUri = ImageUtils.createTempImageUri(context)
                        tempImageUri = newUri
                        cameraLauncher.launch(newUri)
                    }
                    else -> {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Usuario") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (!ui.isEditing && ui.user != null) {
                        IconButton(
                            onClick = { vm.startEditing() },
                            modifier = Modifier.testTag("btnEdit")
                        ) {
                            Icon(Icons.Default.Edit, "Editar perfil")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padd ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padd)
        ) {
            when {
                ui.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                ui.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            ui.error ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { vm.clearMessages() }) {
                            Text("Reintentar")
                        }
                    }
                }
                ui.user != null -> {
                    if (ui.isEditing) {
                        EditProfileContent(
                            ui = ui,
                            vm = vm,
                            onImagePick = { showImageSourceDialog = true },
                            onSave = {
                                vm.saveChanges(context) { updatedUser ->
                                    sessionViewModel.updateUser(updatedUser)
                                }
                            },
                            onCancel = { vm.cancelEditing() }
                        )
                    } else {
                        ViewProfileContent(
                            ui = ui,
                            onLogout = {
                                scope.launch {
                                    userPrefs.clearLogin()
                                    sessionViewModel.clearSession()
                                    onLogout()
                                }
                            }
                        )
                    }
                }
                else -> {
                    Text(
                        "No hay usuario cargado",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun ViewProfileContent(
    ui: com.example.geekplayproyecto.ui.viewmodel.ProfileUiState,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto de perfil
        val imagePath = ui.user?.profileImagePath
        if (imagePath != null) {
            val resourceId = ImageMapper.getDrawableIdFromName(imagePath)

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
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    ui.user?.name?.firstOrNull()?.toString() ?: "?",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            ui.user?.name ?: "",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            ui.user?.email ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(Icons.Default.Person, "Nombre", ui.user?.name ?: "")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                InfoRow(Icons.Default.Email, "Email", ui.user?.email ?: "")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                InfoRow(Icons.Default.Phone, "Teléfono", ui.user?.phone ?: "")
            }
        }

        if (ui.successMsg != null) {
            Spacer(Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text(
                    ui.successMsg,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
                .testTag("btnLogout")
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun EditProfileContent(
    ui: com.example.geekplayproyecto.ui.viewmodel.ProfileUiState,
    vm: ProfileViewModel,
    onImagePick: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto de perfil editable
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            val editImage = ui.editProfileImageUri
            if (editImage != null) {
                val resourceId = ImageMapper.getDrawableIdFromName(editImage)

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(
                            when {
                                resourceId != null -> resourceId
                                editImage.startsWith("content://") -> editImage.toUri()
                                else -> ImageUtils.getFileUri(editImage)
                            }
                        )
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        ui.editName.firstOrNull()?.toString() ?: "?",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            FloatingActionButton(
                onClick = onImagePick,
                modifier = Modifier.size(40.dp).align(Alignment.BottomEnd),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.CameraAlt, "Cambiar foto", modifier = Modifier.size(20.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = ui.editName,
            onValueChange = { vm.onNameChange(it) },
            label = { Text("Nombre") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            modifier = Modifier.fillMaxWidth()
                .testTag("txtNombreEdit"),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = ui.editPhone,
            onValueChange = { vm.onPhoneChange(it) },
            label = { Text("Teléfono") },
            leadingIcon = { Icon(Icons.Default.Phone, null) },
            modifier = Modifier.fillMaxWidth()
                .testTag("txtTelefonoEdit"),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        Text("Cambiar contraseña (opcional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(8.dp))

        // ✅ CAMPO NUEVO: CONTRASEÑA ACTUAL
        OutlinedTextField(
            value = ui.currentPassword,
            onValueChange = { vm.onCurrentPasswordChange(it) },
            label = { Text("Contraseña actual") },
            leadingIcon = { Icon(Icons.Default.LockOpen, null) },
            trailingIcon = {
                IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                    Icon(if (showCurrentPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
                .testTag("txtPasswordActual"),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = ui.editPassword,
            onValueChange = { vm.onPasswordChange(it) },
            label = { Text("Nueva contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
                .testTag("txtPasswordNueva"),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = ui.editConfirmPassword,
            onValueChange = { vm.onConfirmPasswordChange(it) },
            label = { Text("Confirmar contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                    Icon(if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
                .testTag("txtPasswordConfirmar"),
            singleLine = true
        )

        if (ui.error != null) {
            Spacer(Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text(ui.error!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(16.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)
                .testTag("btnCancelar")) { Text("Cancelar") }
            Button(onClick = onSave, modifier = Modifier.weight(1f)
                .testTag("btnGuardar")) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar")
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onGallery: () -> Unit,
    onCamera: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar fuente de imagen") },
        text = { Text("¿Desde dónde quieres seleccionar la imagen?") },
        confirmButton = {
            Column(Modifier.fillMaxWidth()) {
                TextButton(onClick = onGallery, modifier = Modifier.fillMaxWidth()) { Text("Galería") }
                TextButton(onClick = onCamera, modifier = Modifier.fillMaxWidth()) { Text("Cámara") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}