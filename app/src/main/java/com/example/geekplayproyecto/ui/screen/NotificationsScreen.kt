package com.example.geekplayproyecto.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geekplayproyecto.data.local.ban.BanNotification
import com.example.geekplayproyecto.ui.viewmodel.BanNotificationViewModel
import com.example.geekplayproyecto.ui.viewmodel.BanNotificationViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    factory: BanNotificationViewModelFactory,
    onNavigateBack: () -> Unit
) {
    val vm: BanNotificationViewModel = viewModel(factory = factory)
    val notifications by vm.banNotifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones de Baneo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("notificationsList"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (notifications.isEmpty()) {
                item {
                    Text("No tienes notificaciones.",
                        modifier = Modifier.testTag("txtNoNotifications"))
                }
            } else {
                items(notifications) { notification ->
                    //  Llamada correcta a la función externa
                    BanNotificationItem(
                        notification = notification,
                        onDelete = { vm.deleteNotification(notification.id) }
                    )
                }
            }
        }
    }
} // ⬅️ ESTA LLAVE CIERRA NotificationsScreen

// BanNotificationItem debe estar AFUERA, aquí abajo:
@Composable
private fun BanNotificationItem(
    notification: BanNotification,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Motivo:", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                }
                // Botón de Borrar
                IconButton(onClick = onDelete,
                    modifier = Modifier.testTag("btnDelete")) {
                    Icon(Icons.Default.Delete, "Borrar notificación", tint = MaterialTheme.colorScheme.error)
                }
            }

            Text(notification.reason)
            Spacer(Modifier.height(8.dp))

            Row {
                Text("Duración:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Text(notification.duration)
            }
            Spacer(Modifier.height(8.dp))

            Column {
                Text("Guía para apelar:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(notification.appealGuide)
            }
        }
    }
}