package com.example.geekplayproyecto.domain.validation

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ValidatorsTest {

    // ========== TESTS DE EMAIL ==========
    @Test
    fun `validateEmail con email vacio debe retornar error`() {
        val result = validateEmail("")
        assertEquals("El email es obligatorio", result)
    }

    @Test
    fun `validateEmail con email en blanco debe retornar error`() {
        val result = validateEmail("   ")
        assertEquals("El email es obligatorio", result)
    }

    @Test
    fun `validateEmail con formato invalido debe retornar error`() {
        val result = validateEmail("emailsinformato")
        assertEquals("Formato de email inválido", result)
    }

    @Test
    fun `validateEmail con email valido debe retornar null`() {
        val result = validateEmail("ricardo@geekplay.com")
        assertNull(result)
    }

    // ========== TESTS DE NOMBRE ==========
    @Test
    fun `validateNameLettersOnly con nombre vacio debe retornar error`() {
        val result = validateNameLettersOnly("")
        assertEquals("El nombre es obligatorio", result)
    }

    @Test
    fun `validateNameLettersOnly con numeros debe retornar error`() {
        val result = validateNameLettersOnly("Ricardo123")
        assertEquals("Solo letras y espacios", result)
    }

    @Test
    fun `validateNameLettersOnly con nombre valido debe retornar null`() {
        val result = validateNameLettersOnly("Ricardo")
        assertNull(result)
    }

    @Test
    fun `validateNameLettersOnly con tildes debe retornar null`() {
        val result = validateNameLettersOnly("José María")
        assertNull(result)
    }

    // ========== TESTS DE TELÉFONO ==========
    @Test
    fun `validatePhoneDigitsOnly con telefono vacio debe retornar error`() {
        val result = validatePhoneDigitsOnly("")
        assertEquals("El teléfono es obligatorio", result)
    }

    @Test
    fun `validatePhoneDigitsOnly con letras debe retornar error`() {
        val result = validatePhoneDigitsOnly("12345abc")
        assertEquals("Solo números", result)
    }

    @Test
    fun `validatePhoneDigitsOnly con menos de 8 digitos debe retornar error`() {
        val result = validatePhoneDigitsOnly("1234567")
        assertEquals("Debe tener entre 8 y 15 dígitos", result)
    }

    @Test
    fun `validatePhoneDigitsOnly con telefono chileno debe retornar null`() {
        val result = validatePhoneDigitsOnly("912345678")
        assertNull(result)
    }

    // ========== TESTS DE CONTRASEÑA ==========
    @Test
    fun `validateStrongPassword con password vacia debe retornar error`() {
        val result = validateStrongPassword("")
        assertEquals("La contraseña es obligatoria", result)
    }

    @Test
    fun `validateStrongPassword con menos de 8 caracteres debe retornar error`() {
        val result = validateStrongPassword("Abc1@")
        assertEquals("Mínimo 8 caracteres", result)
    }

    @Test
    fun `validateStrongPassword sin mayuscula debe retornar error`() {
        val result = validateStrongPassword("abc12345@")
        assertEquals("Debe incluir una mayúscula", result)
    }

    @Test
    fun `validateStrongPassword sin minuscula debe retornar error`() {
        val result = validateStrongPassword("ABC12345@")
        assertEquals("Debe incluir una minúscula", result)
    }

    @Test
    fun `validateStrongPassword sin numero debe retornar error`() {
        val result = validateStrongPassword("Abcdefgh@")
        assertEquals("Debe incluir un número", result)
    }

    @Test
    fun `validateStrongPassword sin simbolo debe retornar error`() {
        val result = validateStrongPassword("Abc12345")
        assertEquals("Debe incluir un símbolo", result)
    }

    @Test
    fun `validateStrongPassword con espacios debe retornar error`() {
        val result = validateStrongPassword("Abc 123@")
        assertEquals("No debe contener espacios", result)
    }

    @Test
    fun `validateStrongPassword valida debe retornar null`() {
        val result = validateStrongPassword("GeekPlay123@")
        assertNull(result)
    }

    // ========== TESTS DE CONFIRMACIÓN ==========
    @Test
    fun `validateConfirm con confirmacion vacia debe retornar error`() {
        val result = validateConfirm("GeekPlay123@", "")
        assertEquals("Confirma tu contraseña", result)
    }

    @Test
    fun `validateConfirm con passwords diferentes debe retornar error`() {
        val result = validateConfirm("GeekPlay123@", "DifferentPass1@")
        assertEquals("Las contraseñas no coinciden", result)
    }

    @Test
    fun `validateConfirm con passwords iguales debe retornar null`() {
        val result = validateConfirm("GeekPlay123@", "GeekPlay123@")
        assertNull(result)
    }
}