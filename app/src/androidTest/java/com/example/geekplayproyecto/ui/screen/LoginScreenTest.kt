package com.example.geekplayproyecto.ui.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_mostrarElementosPrincipales() {
        composeTestRule.setContent {
            LoginScreen(
                email = "",
                pass = "",
                emailError = null,
                passError = null,
                canSubmit = false,
                isSubmitting = false,
                errorMsg = null,
                onEmailChange = {},
                onPassChange = {},
                onSubmit = {},
                onGoRegister = {}
            )
        }

        // Verificar usando testTags
        composeTestRule.onNodeWithTag("txtEmail").assertExists()
        composeTestRule.onNodeWithTag("txtPassword").assertExists()
        composeTestRule.onNodeWithTag("btnLogin").assertExists()
        composeTestRule.onNodeWithTag("btnGoRegister").assertExists()
    }

    @Test
    fun loginScreen_camposVacios_botonDeshabilitado() {
        composeTestRule.setContent {
            LoginScreen(
                email = "",
                pass = "",
                emailError = null,
                passError = null,
                canSubmit = false,
                isSubmitting = false,
                errorMsg = null,
                onEmailChange = {},
                onPassChange = {},
                onSubmit = {},
                onGoRegister = {}
            )
        }

        composeTestRule
            .onNodeWithTag("btnLogin")
            .assertIsNotEnabled()
    }

    @Test
    fun loginScreen_camposLlenos_botonHabilitado() {
        composeTestRule.setContent {
            LoginScreen(
                email = "ricardo@geekplay.com",
                pass = "GeekPlay123@",
                emailError = null,
                passError = null,
                canSubmit = true,
                isSubmitting = false,
                errorMsg = null,
                onEmailChange = {},
                onPassChange = {},
                onSubmit = {},
                onGoRegister = {}
            )
        }

        composeTestRule
            .onNodeWithTag("btnLogin")
            .assertIsEnabled()
    }

    @Test
    fun loginScreen_emailInvalido_muestraError() {
        composeTestRule.setContent {
            LoginScreen(
                email = "emailinvalido",
                pass = "",
                emailError = "Formato de email inválido",
                passError = null,
                canSubmit = false,
                isSubmitting = false,
                errorMsg = null,
                onEmailChange = {},
                onPassChange = {},
                onSubmit = {},
                onGoRegister = {}
            )
        }

        composeTestRule
            .onNodeWithText("Formato de email inválido")
            .assertExists()
    }

    @Test
    fun loginScreen_passwordDebil_muestraError() {
        composeTestRule.setContent {
            LoginScreen(
                email = "ricardo@geekplay.com",
                pass = "123",
                emailError = null,
                passError = "Mínimo 8 caracteres",
                canSubmit = false,
                isSubmitting = false,
                errorMsg = null,
                onEmailChange = {},
                onPassChange = {},
                onSubmit = {},
                onGoRegister = {}
            )
        }

        composeTestRule
            .onNodeWithText("Mínimo 8 caracteres")
            .assertExists()
    }

    @Test
    fun loginScreen_enviando_muestraValidando() {
        composeTestRule.setContent {
            LoginScreen(
                email = "ricardo@geekplay.com",
                pass = "GeekPlay123@",
                emailError = null,
                passError = null,
                canSubmit = true,
                isSubmitting = true,
                errorMsg = null,
                onEmailChange = {},
                onPassChange = {},
                onSubmit = {},
                onGoRegister = {}
            )
        }

        // El botón muestra "Validando..." cuando está enviando
        composeTestRule
            .onNodeWithText("Validando...")
            .assertExists()
    }

    @Test
    fun loginScreen_errorServidor_muestraMensaje() {
        composeTestRule.setContent {
            LoginScreen(
                email = "ricardo@geekplay.com",
                pass = "GeekPlay123@",
                emailError = null,
                passError = null,
                canSubmit = true,
                isSubmitting = false,
                errorMsg = "Credenciales incorrectas",
                onEmailChange = {},
                onPassChange = {},
                onSubmit = {},
                onGoRegister = {}
            )
        }

        composeTestRule
            .onNodeWithText("Credenciales incorrectas")
            .assertExists()
    }
}