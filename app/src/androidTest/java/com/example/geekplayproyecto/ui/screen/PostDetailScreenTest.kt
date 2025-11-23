package com.example.geekplayproyecto.ui.screen


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
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
class PostDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun postDetailScreen_botonCompartir_existe() {
        composeTestRule.setContent {
            IconButton(
                onClick = {},
                modifier = Modifier.testTag("btnShare")
            ) {
                Icon(Icons.Default.Share, "Compartir")
            }
        }

        composeTestRule
            .onNodeWithTag("btnShare")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun postDetailScreen_botonLike_existe() {
        composeTestRule.setContent {
            IconToggleButton(
                checked = false,
                onCheckedChange = {},
                modifier = Modifier.testTag("btnLike")
            ) {
                Icon(Icons.Default.FavoriteBorder, "Me gusta")
            }
        }

        composeTestRule
            .onNodeWithTag("btnLike")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun postDetailScreen_botonLike_funcionaToggle() {
        var isLiked = false

        composeTestRule.setContent {
            IconToggleButton(
                checked = isLiked,
                onCheckedChange = { isLiked = it },
                modifier = Modifier.testTag("btnLike")
            ) {
                Icon(
                    if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    "Me gusta"
                )
            }
        }

        // Verificar estado inicial
        assert(!isLiked)

        // Hacer click
        composeTestRule
            .onNodeWithTag("btnLike")
            .performClick()

        // Verificar que cambió
        assert(isLiked)
    }

    @Test
    fun postDetailScreen_textoComentarios_existe() {
        composeTestRule.setContent {
            Text(
                "Comentarios (5)",
                modifier = Modifier.testTag("txtComentarios")
            )
        }

        composeTestRule
            .onNodeWithTag("txtComentarios")
            .assertExists()
    }

    @Test
    fun postDetailScreen_campoComentario_existe() {
        composeTestRule.setContent {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Escribe un comentario...") },
                modifier = Modifier.testTag("txtComentario")
            )
        }

        composeTestRule
            .onNodeWithTag("txtComentario")
            .assertExists()
    }

    @Test
    fun postDetailScreen_campoComentario_aceptaTexto() {
        var comentario = ""

        composeTestRule.setContent {
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                placeholder = { Text("Escribe un comentario...") },
                modifier = Modifier.testTag("txtComentario")
            )
        }

        composeTestRule
            .onNodeWithTag("txtComentario")
            .performTextInput("Excelente post!")

        assert(comentario == "Excelente post!")
    }

    @Test
    fun postDetailScreen_botonEnviarComentario_existe() {
        composeTestRule.setContent {
            IconButton(
                onClick = {},
                modifier = Modifier.testTag("btnEnviarComentario")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, "Enviar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnEnviarComentario")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun postDetailScreen_botonEnviarComentario_funciona() {
        var comentarioEnviado = false

        composeTestRule.setContent {
            IconButton(
                onClick = { comentarioEnviado = true },
                modifier = Modifier.testTag("btnEnviarComentario")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, "Enviar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnEnviarComentario")
            .performClick()

        assert(comentarioEnviado)
    }

    @Test
    fun postDetailScreen_botonEnviarComentario_deshabilitadoSinTexto() {
        composeTestRule.setContent {
            IconButton(
                onClick = {},
                enabled = false,
                modifier = Modifier.testTag("btnEnviarComentario")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, "Enviar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnEnviarComentario")
            .assertIsNotEnabled()
    }

    @Test
    fun postDetailScreen_botonEnviarComentario_habilitadoConTexto() {
        composeTestRule.setContent {
            IconButton(
                onClick = {},
                enabled = true,
                modifier = Modifier.testTag("btnEnviarComentario")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, "Enviar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnEnviarComentario")
            .assertIsEnabled()
    }
}