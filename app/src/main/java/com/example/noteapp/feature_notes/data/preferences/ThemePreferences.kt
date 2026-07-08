package com.example.noteapp.feature_notes.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("aurora_prefs")
private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

@Singleton
class ThemePreferences @Inject constructor(@ApplicationContext private val context: Context) {
    val isDarkTheme = context.dataStore.data.map { it[DARK_THEME_KEY] ?: true }
    suspend fun setDarkTheme(dark: Boolean) {
        context.dataStore.edit { it[DARK_THEME_KEY] = dark }
    }
}