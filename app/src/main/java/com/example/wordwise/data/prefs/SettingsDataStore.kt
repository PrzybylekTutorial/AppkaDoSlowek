package com.example.wordwise.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.wordwise.ui.theme.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    private val THEME_PREF = intPreferencesKey("theme_pref")

    val themePreference: Flow<ThemePreference> = context.dataStore.data.map { prefs ->
        when (prefs[THEME_PREF] ?: 0) {
            1 -> ThemePreference.LIGHT
            2 -> ThemePreference.DARK
            else -> ThemePreference.SYSTEM
        }
    }

    suspend fun setThemePreference(pref: ThemePreference) {
        context.dataStore.edit { prefs: Preferences ->
            prefs[THEME_PREF] = when (pref) {
                ThemePreference.SYSTEM -> 0
                ThemePreference.LIGHT -> 1
                ThemePreference.DARK -> 2
            }
        }
    }
}