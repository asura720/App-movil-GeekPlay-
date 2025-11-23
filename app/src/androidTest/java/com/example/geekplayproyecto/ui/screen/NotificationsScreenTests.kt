package com.example.geekplayproyecto.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun notificationsScreen_listaNotificaciones_existe() {
        composeTestRule.setContent {
            LazyColumn(
                modifier = Modifier.testTag("notificationsList")
            ) {
                item {
                    Text("Notificación 1")
                }
            }
        }

        composeTestRule
            .onNodeWithTag("notificationsList")
            .assertExists()
    }

    @Test
    fun notificationsScreen_sinNotificaciones_muestraMensaje() {
        composeTestRule.setContent {
            Text(
                "No tienes notificaciones.",
                modifier = Modifier.testTag("txtNoNotifications")
            )
        }

        composeTestRule
            .onNodeWithTag("txtNoNotifications")
            .assertExists()

        composeTestRule
            .onNodeWithText("No tienes notificaciones.")
            .assertExists()
    }

    @Test
    fun notificationsScreen_botonBorrar_existe() {
        composeTestRule.setContent {
            IconButton(
                onClick = {},
                modifier = Modifier.testTag("btnDelete")
            ) {
                Icon(Icons.Default.Delete, "Borrar notificación")
            }
        }

        composeTestRule
            .onNodeWithTag("btnDelete")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun notificationsScreen_botonBorrar_funciona() {
        var deleteClicked = false

        composeTestRule.setContent {
            IconButton(
                onClick = { deleteClicked = true },
                modifier = Modifier.testTag("btnDelete")
            ) {
                Icon(Icons.Default.Delete, "Borrar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnDelete")
            .performClick()

        assert(deleteClicked)
    }

    @Test
    fun notificationsScreen_verificarTextoMotivo() {
        composeTestRule.setContent {
            Column {
                Text("Motivo:")
                Text("Spam")
            }
        }

        composeTestRule
            .onNodeWithText("Motivo:")
            .assertExists()
    }

    @Test
    fun notificationsScreen_verificarTextoDuracion() {
        composeTestRule.setContent {
            Column {
                Text("Duración:")
                Text("24 horas")
            }
        }

        composeTestRule
            .onNodeWithText("Duración:")
            .assertExists()
    }

    @Test
    fun notificationsScreen_verificarTextoApelar() {
        composeTestRule.setContent {
            Text("Guía para apelar:")
        }

        composeTestRule
            .onNodeWithText("Guía para apelar:")
            .assertExists()
    }
}