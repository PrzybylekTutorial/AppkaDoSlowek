package com.example.wordwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordwise.ui.navigation.WordWiseNavHost
import com.example.wordwise.ui.theme.WordWiseTheme
import com.example.wordwise.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.provideFactory(applicationContext))

            WordWiseTheme(themePreferenceFlow = settingsViewModel.themePreferenceFlow) {
                val snackbarHostState = remember { SnackbarHostState() }
                Surface {
                    WordWiseNavHost(
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}