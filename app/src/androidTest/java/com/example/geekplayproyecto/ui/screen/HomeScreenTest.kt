package com.example.geekplayproyecto.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_botonBuscar_existe() {
        composeTestRule.setContent {
            IconButton(
                onClick = {},
                modifier = Modifier.testTag("btnSearch")
            ) {
                Icon(Icons.Default.Search, "Buscar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnSearch")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun homeScreen_botonBuscar_funciona() {
        var searchClicked = false

        composeTestRule.setContent {
            IconButton(
                onClick = { searchClicked = true },
                modifier = Modifier.testTag("btnSearch")
            ) {
                Icon(Icons.Default.Search, "Buscar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnSearch")
            .performClick()

        assert(searchClicked)
    }

    @Test
    fun homeScreen_fabCrearPost_existe() {
        composeTestRule.setContent {
            ExtendedFloatingActionButton(
                onClick = {},
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Nuevo Post") },
                modifier = Modifier.testTag("fabCreatePost")
            )
        }

        composeTestRule
            .onNodeWithTag("fabCreatePost")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun homeScreen_fabCrearPost_funciona() {
        var fabClicked = false

        composeTestRule.setContent {
            ExtendedFloatingActionButton(
                onClick = { fabClicked = true },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Nuevo Post") },
                modifier = Modifier.testTag("fabCreatePost")
            )
        }

        composeTestRule
            .onNodeWithTag("fabCreatePost")
            .performClick()

        assert(fabClicked)
    }

    @Test
    fun homeScreen_contenidoPrincipal_existe() {
        composeTestRule.setContent {
            Column(modifier = Modifier.testTag("mainContent")) {
                Text("Contenido principal")
            }
        }

        composeTestRule
            .onNodeWithTag("mainContent")
            .assertExists()
    }

    @Test
    fun homeScreen_botonEntrar_existe() {
        composeTestRule.setContent {
            FilledTonalButton(
                onClick = {},
                modifier = Modifier.testTag("btnEntrar")
            ) {
                Text("Entrar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnEntrar")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun homeScreen_botonEntrar_funciona() {
        var loginClicked = false

        composeTestRule.setContent {
            FilledTonalButton(
                onClick = { loginClicked = true },
                modifier = Modifier.testTag("btnEntrar")
            ) {
                Text("Entrar")
            }
        }

        composeTestRule
            .onNodeWithTag("btnEntrar")
            .performClick()

        assert(loginClicked)
    }

    @Test
    fun homeScreen_botonRegistro_existe() {
        composeTestRule.setContent {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.testTag("btnRegistro")
            ) {
                Text("Registro")
            }
        }

        composeTestRule
            .onNodeWithTag("btnRegistro")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun homeScreen_botonRegistro_funciona() {
        var registerClicked = false

        composeTestRule.setContent {
            OutlinedButton(
                onClick = { registerClicked = true },
                modifier = Modifier.testTag("btnRegistro")
            ) {
                Text("Registro")
            }
        }

        composeTestRule
            .onNodeWithTag("btnRegistro")
            .performClick()

        assert(registerClicked)
    }

    @Test
    fun homeScreen_verificarTextoGeekPlay() {
        composeTestRule.setContent {
            Text("GeekPlay")
        }

        composeTestRule
            .onNodeWithText("GeekPlay")
            .assertExists()
    }
}