package com.example.geekplayproyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.geekplayproyecto.data.local.storage.UserPreferences
import com.example.geekplayproyecto.navigation.AppNavGraph
import com.example.geekplayproyecto.ui.theme.GeekPlayProyectoTheme
import com.example.geekplayproyecto.ui.viewmodel.AuthViewModel
import com.example.geekplayproyecto.ui.viewmodel.AuthViewModelFactory
import com.example.geekplayproyecto.ui.viewmodel.SessionViewModel
import com.example.geekplayproyecto.ui.viewmodel.SessionViewModelFactory
import com.example.geekplayproyecto.utils.ServiceLocator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current.applicationContext
    val userPrefs = remember { UserPreferences(context) }
    val theme by userPrefs.theme.collectAsState(initial = "system")

    val useDarkTheme = when (theme) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val userRepository = ServiceLocator.provideUserRepository(context)

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(userRepository))
    val sessionViewModel: SessionViewModel = viewModel(factory = SessionViewModelFactory(userRepository))

    val navController = rememberNavController()

    GeekPlayProyectoTheme(darkTheme = useDarkTheme, dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                sessionViewModel = sessionViewModel
            )
        }
    }
}