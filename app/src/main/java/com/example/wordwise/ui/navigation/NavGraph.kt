package com.example.wordwise.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wordwise.ui.screens.edit.AddEditWordScreen
import com.example.wordwise.ui.screens.learn.LearnScreen
import com.example.wordwise.ui.screens.list.WordListScreen
import com.example.wordwise.ui.screens.settings.SettingsScreen

sealed class Destinations(val route: String) {
    data object List : Destinations("list")
    data object AddEdit : Destinations("addEdit?wordId={wordId}")
    data object Learn : Destinations("learn")
    data object Settings : Destinations("settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordWiseNavHost(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WordWise") },
                actions = {
                    IconButton(onClick = { navController.navigate(Destinations.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Destinations.List.route,
        ) {
            composable(Destinations.List.route) {
                WordListScreen(
                    onAddNew = { navController.navigate("addEdit") },
                    onLearn = { navController.navigate(Destinations.Learn.route) },
                    onEdit = { id -> navController.navigate("addEdit?wordId=$id") }
                )
            }
            composable(
                route = Destinations.AddEdit.route,
                arguments = listOf(
                    navArgument("wordId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val id: Long? = backStackEntry.arguments?.getString("wordId")?.toLongOrNull()
                AddEditWordScreen(wordId = id, onDone = { navController.popBackStack() })
            }
            composable(Destinations.Learn.route) {
                LearnScreen(onClose = { navController.popBackStack() })
            }
            composable(Destinations.Settings.route) {
                SettingsScreen()
            }
        }
    }
}