package com.example.geekplayproyecto.ui.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registerScreen_mostrarTodosCampos() {
        composeTestRule.setContent {
            RegisterScreen(
                name = "",
                email = "",
                phone = "",
                pass = "",
                confirm = "",
                nameError = null,
                emailError = null,
                phoneError = null,
                passError = null,
                confirmError = null,
                canSubmit = false,
                isSubmitting = false,
                errorMsg = null,
                onNameChange = {},
                onEmailChange = {},
                onPhoneChange = {},
                onPassChange = {},
                onConfirmChange = {},
                onSubmit = {},
                onGoLogin = {}
            )
        }

        // Verificar que todos los campos existen
        composeTestRule.onNodeWithTag("txtNombre").assertExists()
        composeTestRule.onNodeWithTag("txtEmail").assertExists()
        composeTestRule.onNodeWithTag("txtTelefono").assertExists()
        composeTestRule.onNodeWithTag("txtPassword").assertExists()
        composeTestRule.onNodeWithTag("txtConfirmar").assertExists()
        composeTestRule.onNodeWithTag("btnRegistrar").assertExists()
        composeTestRule.onNodeWithTag("btnGoLogin").assertExists()
    }

    @Test
    fun registerScreen_camposVacios_botonDeshabilitado() {
        composeTestRule.setContent {
            RegisterScreen(
                name = "",
                email = "",
                phone = "",
                pass = "",
                confirm = "",
                nameError = null,
                emailError = null,
                phoneError = null,
                passError = null,
                confirmError = null,
                canSubmit = false,
                isSubmitting = false,
                errorMsg = null,
                onNameChange = {},
                onEmailChange = {},
                onPhoneChange = {},
                onPassChange = {},
                onConfirmChange = {},
                onSubmit = {},
                onGoLogin = {}
            )
        }

        composeTestRule
            .onNodeWithTag("btnRegistrar")
            .assertIsNotEnabled()
    }

    @Test
    fun registerScreen_todosLosCamposLlenos_botonHabilitado() {
        composeTestRule.setContent {
            RegisterScreen(
                name = "Ricardo",
                email = "ricardo@geekplay.com",
                phone = "912345678",
                pass = "GeekPlay123@",
                confirm = "GeekPlay123@",
                nameError = null,
                emailError = null,
                phoneError = null,
                passError = null,
                confirmError = null,
                canSubmit = true,
                isSubmitting = false,
                errorMsg = null,
                onNameChange = {},
                onEmailChange = {},
                onPhoneChange = {},
                onPassChange = {},
                onConfirmChange = {},
                onSubmit = {},
                onGoLogin = {}
            )
        }

        composeTestRule
            .onNodeWithTag("btnRegistrar")
            .assertIsEnabled()
    }

    @Test
    fun registerScreen_nombreConNumeros_muestraError() {
        composeTestRule.setContent {
            RegisterScreen(
                name = "Ricardo123",
                email = "",
                phone = "",
                pass = "",
                confirm = "",
                nameError = "Solo letras y espacios",
                emailError = null,
                phoneError = null,
                passError = null,
                confirmError = null,
                canSubmit = false,
                isSubmitting = false,
                errorMsg = null,
                onNameChange = {},
                onEmailChange = {},
                onPhoneChange = {},
                onPassChange = {},
                onConfirmChange = {},
                onSubmit = {},
                onGoLogin = {}
            )
        }

        composeTestRule
            .onNodeWithText("Solo letras y espacios")
            .assertExists()
    }

    @Test
    fun registerScreen_emailInvalido_muestraError() {
        composeTestRule.setContent {
            RegisterScreen(
                name = "Ricardo",
                email = "emailinvalido",
                phone = "",
                pass = "",
                confirm = "",
                nameError = null,
                emailError = "Formato de email inválido",
                phoneError = null,
                passError = null,
                confirmError = null,
                canSubmit = false,
                isSubmitting = false,
                errorMsg = null,
                onNameChange = {},
                onEmailChange = {},
                onPhoneChange = {},
                onPassChange = {},
                onConfirmChange = {},
                onSubmit = {},
                onGoLogin = {}
            )
        }

        composeTestRule
            .onNodeWithText("Formato de email inválido")
            .assertExists()
    }

    @Test
    fun registerScreen_telefonoMuyCorto_muestraError() {
        composeTestRule.setContent {
            RegisterScreen(
                name = "Ricardo",
                email = "ricardo@geekplay.com",
                phone = "123",
                pass = "",
                confirm = "",
                nameError = null,
                emailError = null,
                phoneError = "Debe tener entre 8 y 15 dígitos",
                passError = null,
                confirmError = null,
                canSubmit = false,
                isSubmitting = false,
                errorMsg = null,
                onNameChange = {},
                onEmailChange = {},
                onPhoneChange = {},
                onPassChange = {},
                onConfirmChange = {},
                onSubmit = {},
                onGoLogin = {}
            )
        }

        composeTestRule
            .onNodeWithText("Debe tener entre 8 y 15 dígitos")
            .assertExists()
    }

    @Test
    fun registerScreen_passwordDebil_muestraError() {
        composeTestRule.setContent {
            RegisterScreen(
                name = "Ricardo",
                email = "ricardo@geekplay.com",
                phone = "912345678",
                pass = "123",
                confirm = "",
                nameError = null,
                emailError = null,
                phoneError = null,
                passError = "Mínimo 8 caracteres",
                confirmError = null,
                canSubmit = false,
                isSubmitting = false,
                errorMsg = null,
                onNameChange = {},
                onEmailChange = {},
                onPhoneChange = {},
                onPassChange = {},
                onConfirmChange = {},
                onSubmit = {},
                onGoLogin = {}
            )
        }

        composeTestRule
            .onNodeWithText("Mínimo 8 caracteres")
            .assertExists()
    }

    @Test
    fun registerScreen_passwordsNoCoinciden_muestraError() {
        composeTestRule.setContent {
            RegisterScreen(
                name = "Ricardo",
                email = "ricardo@geekplay.com",
                phone = "912345678",
                pass = "GeekPlay123@",
                confirm = "DifferentPass1@",
                nameError = null,
                emailError = null,
                phoneError = null,
                passError = null,
                confirmError = "Las contraseñas no coinciden",
                canSubmit = false,
                isSubmitting = false,
                errorMsg = null,
                onNameChange = {},
                onEmailChange = {},
                onPhoneChange = {},
                onPassChange = {},
                onConfirmChange = {},
                onSubmit = {},
                onGoLogin = {}
            )
        }

        composeTestRule
            .onNodeWithText("Las contraseñas no coinciden")
            .assertExists()
    }

    @Test
    fun registerScreen_enviando_muestraCreandoCuenta() {
        composeTestRule.setContent {
            RegisterScreen(
                name = "Ricardo",
                email = "ricardo@geekplay.com",
                phone = "912345678",
                pass = "GeekPlay123@",
                confirm = "GeekPlay123@",
                nameError = null,
                emailError = null,
                phoneError = null,
                passError = null,
                confirmError = null,
                canSubmit = true,
                isSubmitting = true,
                errorMsg = null,
                onNameChange = {},
                onEmailChange = {},
                onPhoneChange = {},
                onPassChange = {},
                onConfirmChange = {},
                onSubmit = {},
                onGoLogin = {}
            )
        }

        composeTestRule
            .onNodeWithText("Creando cuenta...")
            .assertExists()
    }
}