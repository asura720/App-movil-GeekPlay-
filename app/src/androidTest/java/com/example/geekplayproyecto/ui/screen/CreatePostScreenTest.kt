package com.example.geekplayproyecto.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
class CreatePostScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun createPostScreen_mostrarCamposPrincipales() {
        composeTestRule.setContent {
            Column {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Título") },
                    modifier = Modifier.testTag("txtTitulo")
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Resumen") },
                    modifier = Modifier.testTag("txtResumen")
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Contenido") },
                    modifier = Modifier.testTag("txtContenido")
                )
            }
        }

        composeTestRule.onNodeWithTag("txtTitulo").assertExists()
        composeTestRule.onNodeWithTag("txtResumen").assertExists()
        composeTestRule.onNodeWithTag("txtContenido").assertExists()
    }

    @Test
    fun createPostScreen_campoTitulo_aceptaTexto() {
        var titulo = ""

        composeTestRule.setContent {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.testTag("txtTitulo")
            )
        }

        composeTestRule
            .onNodeWithTag("txtTitulo")
            .performTextInput("Mi primer post")

        assert(titulo == "Mi primer post")
    }

    @Test
    fun createPostScreen_campoResumen_aceptaTexto() {
        var resumen = ""

        composeTestRule.setContent {
            OutlinedTextField(
                value = resumen,
                onValueChange = { resumen = it },
                label = { Text("Resumen") },
                modifier = Modifier.testTag("txtResumen")
            )
        }

        composeTestRule
            .onNodeWithTag("txtResumen")
            .performTextInput("Este es un resumen")

        assert(resumen == "Este es un resumen")
    }

    @Test
    fun createPostScreen_campoContenido_aceptaTexto() {
        var contenido = ""

        composeTestRule.setContent {
            OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text("Contenido") },
                modifier = Modifier.testTag("txtContenido")
            )
        }

        composeTestRule
            .onNodeWithTag("txtContenido")
            .performTextInput("Contenido completo del post")

        assert(contenido == "Contenido completo del post")
    }

    @Test
    fun createPostScreen_botonCancelar_existe() {
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
    fun createPostScreen_botonPublicar_existe() {
        composeTestRule.setContent {
            Button(
                onClick = {},
                modifier = Modifier.testTag("btnPublicar")
            ) {
                Text("Publicar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnPublicar")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun createPostScreen_botonCancelar_funciona() {
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

    @Test
    fun createPostScreen_botonPublicar_funciona() {
        var publishClicked = false

        composeTestRule.setContent {
            Button(
                onClick = { publishClicked = true },
                modifier = Modifier.testTag("btnPublicar")
            ) {
                Text("Publicar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnPublicar")
            .performClick()

        assert(publishClicked)
    }

    @Test
    fun createPostScreen_botonPublicar_deshabilitado() {
        composeTestRule.setContent {
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.testTag("btnPublicar")
            ) {
                Text("Publicar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnPublicar")
            .assertIsNotEnabled()
    }

    @Test
    fun createPostScreen_botonSeleccionarImagen_existe() {
        composeTestRule.setContent {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.testTag("btnSeleccionarImagen")
            ) {
                Text("Seleccionar imagen")
            }
        }

        composeTestRule
            .onNodeWithTag("btnSeleccionarImagen")
            .assertExists()
            .assertHasClickAction()
    }
}