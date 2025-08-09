package com.example.wordwise.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordwise.ui.theme.ThemePreference
import com.example.wordwise.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.provideFactory(androidx.compose.ui.platform.LocalContext.current))
) {
    val theme by viewModel.themePreferenceFlow.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Ustawienia", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Text("Motyw")
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = theme == ThemePreference.SYSTEM, onClick = { viewModel.setThemePreference(ThemePreference.SYSTEM) }, label = { Text("Systemowy") })
            FilterChip(selected = theme == ThemePreference.LIGHT, onClick = { viewModel.setThemePreference(ThemePreference.LIGHT) }, label = { Text("Jasny") })
            FilterChip(selected = theme == ThemePreference.DARK, onClick = { viewModel.setThemePreference(ThemePreference.DARK) }, label = { Text("Ciemny") })
        }
    }
}