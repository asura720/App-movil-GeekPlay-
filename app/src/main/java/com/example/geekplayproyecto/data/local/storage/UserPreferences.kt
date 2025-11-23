package com.example.geekplayproyecto.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(context: Context) {

    private val appContext = context.applicationContext

    // Guardamos el ID también
    val userId: Flow<Long> = appContext.dataStore.data.map {
        it[KEY_USER_ID] ?: -1L
    }

    val lastEmail: Flow<String?> = appContext.dataStore.data.map {
        it[KEY_LAST_EMAIL]
    }

    val isLoggedIn: Flow<Boolean> = appContext.dataStore.data.map {
        (it[KEY_USER_ID] ?: -1L) != -1L // Si hay ID, está logueado
    }

    val theme: Flow<String> = appContext.dataStore.data.map {
        it[KEY_THEME] ?: "system"
    }

    suspend fun saveLogin(id: Long, email: String) {
        appContext.dataStore.edit {
            it[KEY_USER_ID] = id
            it[KEY_LAST_EMAIL] = email
        }
    }

    suspend fun clearLogin() {
        appContext.dataStore.edit {
            it.remove(KEY_USER_ID)
            it.remove(KEY_LAST_EMAIL)
        }
    }

    suspend fun saveTheme(theme: String) {
        appContext.dataStore.edit {
            it[KEY_THEME] = theme
        }
    }

    // Método setLoggedIn deprecado, usamos saveLogin/clearLogin
    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        if (!isLoggedIn) clearLogin()
    }

    companion object {
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_LAST_EMAIL = stringPreferencesKey("last_email")
        private val KEY_THEME = stringPreferencesKey("theme")
    }
}