package com.example.geekplayproyecto.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GeekGray, // Naranja a Gris Oscuro
    onPrimary = Color.White,
    primaryContainer = GeekCard, // Naranja Oscuro a Gris más Oscuro
    onPrimaryContainer = Color.White,

    secondary = GeekSecondary, // Violeta
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3E1C71), // Violeta oscuro
    onSecondaryContainer = Color.White,

    tertiary = GeekTertiary, // Celeste
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF004A7F), // Celeste oscuro
    onTertiaryContainer = Color.White,

    background = GeekInk, // Fondo principal oscuro
    onBackground = GeekCream, // Texto sobre fondo oscuro
    surface = GeekGray, // Superficies como cards, menús
    onSurface = Color.White,
    surfaceVariant = GeekCard, // Variante de superficie (cards más oscuras)
    onSurfaceVariant = Color(0xFFCCCCCC),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF989080)
)

private val LightColorScheme = lightColorScheme(
    primary = GeekOrange,
    onPrimary = Color.White,
    primaryContainer = GeekOrangeLight,
    onPrimaryContainer = GeekText,

    secondary = GeekSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9DDFF),
    onSecondaryContainer = Color.Black,

    tertiary = GeekTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC7E7FF),
    onTertiaryContainer = Color.Black,

    background = GeekCream, // Fondo claro
    onBackground = GeekText, // Texto sobre fondo claro
    surface = Color(0xFFFFFBFF),
    onSurface = GeekText,
    surfaceVariant = Color(0xFFECE1CF),
    onSurfaceVariant = GeekText,

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    outline = Color(0xFF7E7667)
)

@Composable
fun GeekPlayProyectoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}