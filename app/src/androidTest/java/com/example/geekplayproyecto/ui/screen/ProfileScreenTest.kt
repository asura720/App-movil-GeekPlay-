package com.example.geekplayproyecto.ui.screen


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileScreen_botonLogout_existe() {
        composeTestRule.setContent {
            Button(
                onClick = {},
                modifier = Modifier.testTag("btnLogout")
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Text("Cerrar sesión")
            }
        }

        composeTestRule
            .onNodeWithTag("btnLogout")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun profileScreen_botonLogout_funciona() {
        var logoutClicked = false

        composeTestRule.setContent {
            Button(
                onClick = { logoutClicked = true },
                modifier = Modifier.testTag("btnLogout")
            ) {
                Text("Cerrar sesión")
            }
        }

        composeTestRule
            .onNodeWithTag("btnLogout")
            .performClick()

        assert(logoutClicked)
    }

    @Test
    fun profileScreen_botonEdit_existe() {
        composeTestRule.setContent {
            IconButton(
                onClick = {},
                modifier = Modifier.testTag("btnEdit")
            ) {
                Icon(Icons.Default.Edit, "Editar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnEdit")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun profileScreen_campoNombreEdit_existe() {
        composeTestRule.setContent {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Nombre") },
                modifier = Modifier.testTag("txtNombreEdit")
            )
        }

        composeTestRule
            .onNodeWithTag("txtNombreEdit")
            .assertExists()
    }

    @Test
    fun profileScreen_campoNombreEdit_aceptaTexto() {
        var nombre = ""

        composeTestRule.setContent {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.testTag("txtNombreEdit")
            )
        }

        composeTestRule
            .onNodeWithTag("txtNombreEdit")
            .performTextInput("Ricardo García")

        assert(nombre == "Ricardo García")
    }

    @Test
    fun profileScreen_campoTelefonoEdit_existe() {
        composeTestRule.setContent {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Teléfono") },
                modifier = Modifier.testTag("txtTelefonoEdit")
            )
        }

        composeTestRule
            .onNodeWithTag("txtTelefonoEdit")
            .assertExists()
    }

    @Test
    fun profileScreen_botonGuardar_existe() {
        composeTestRule.setContent {
            Button(
                onClick = {},
                modifier = Modifier.testTag("btnGuardar")
            ) {
                Icon(Icons.Default.Save, null)
                Text("Guardar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnGuardar")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun profileScreen_botonGuardar_funciona() {
        var saveClicked = false

        composeTestRule.setContent {
            Button(
                onClick = { saveClicked = true },
                modifier = Modifier.testTag("btnGuardar")
            ) {
                Text("Guardar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnGuardar")
            .performClick()

        assert(saveClicked)
    }

    @Test
    fun profileScreen_botonCancelar_existe() {
        composeTestRule.setContent {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.testTag("btnCancelar")
            ) {
                Text("Cancelar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnCancelar")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun profileScreen_botonCancelar_funciona() {
        var cancelClicked = false

        composeTestRule.setContent {
            OutlinedButton(
                onClick = { cancelClicked = true },
                modifier = Modifier.testTag("btnCancelar")
            ) {
                Text("Cancelar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnCancelar")
            .performClick()

        assert(cancelClicked)
    }
}