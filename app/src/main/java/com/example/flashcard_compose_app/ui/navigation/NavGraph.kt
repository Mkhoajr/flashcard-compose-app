package com.example.flashcard_compose_app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.flashcard_compose_app.data.AuthManager
import com.example.flashcard_compose_app.ui.components.MainAppLayout
import com.example.flashcard_compose_app.ui.screens.FlashcardScreen
import com.example.flashcard_compose_app.ui.screens.HomeScreen
import com.example.flashcard_compose_app.ui.screens.LoginScreen
import com.example.flashcard_compose_app.ui.screens.RegisterScreen
import com.example.flashcard_compose_app.ui.screens.UnitManagerScreen
import com.example.flashcard_compose_app.ui.viewmodels.AppViewModelProvider
import com.example.flashcard_compose_app.ui.viewmodels.UnitManagerViewModel
import com.example.flashcard_compose_app.ui.viewmodels.UnitUiState

@Composable
fun NavGraph() {

    val context = LocalContext.current
    val authManager = AuthManager(context)
    val startDestination = if (authManager.isLoggedIn()) Screen.Home.route else Screen.Login.route

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        // Auth Screens (Login & Register - Doesn't have MainAppLayout)
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        // Main App Screens (MainAppLayout)
        composable(Screen.Home.route) {
            MainAppLayout(navController = navController, authManager = authManager) { innerPadding ->
                HomeScreen(
                    navController = navController,
                    userName = authManager.getUserName() ?: "Unknown User",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        composable(
            route = Screen.UnitManager.route,
            arguments = listOf(
                navArgument("deckId") { type = NavType.IntType },
                navArgument("unitTitle") { type = NavType.StringType; nullable = true },
                navArgument("isUnit") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->

            val deckId = backStackEntry.arguments?.getInt("deckId") ?: 0
            val unitTitle = backStackEntry.arguments?.getString("unitTitle") ?: "Unit Title"
            val isUnit = backStackEntry.arguments?.getBoolean("isUnit") ?: false

            val viewModel: UnitManagerViewModel = viewModel(
                factory = AppViewModelProvider.UnitManagerFactory
            )

            val uiState by viewModel.uiState.collectAsState()

            val context = LocalContext.current
            val authManager = AuthManager(context)

            LaunchedEffect(deckId) {

                val userId = authManager.getUserId()?.toIntOrNull() ?: 0

                println("DEBUG: UnitManager loading with deckId=$deckId, unitTitle=$unitTitle, isUnit=$isUnit")

                val finalUnitTitle = if (isUnit) {
                    // If it's a regular unit (e.g., "Unit 01"), we can clean it up to just "Unit01" or "Unit1" to match backend expectations
                    unitTitle
                        .replace(" ", "")
                        .replace("Unit0", "Unit")
                } else {
                    // If it's not a unit (e.g., virtual deck), we can pass an empty string or the original title based on your backend needs
                    ""
                }

                println("DEBUG: Gọi API với deckId=$deckId, DB Unit=$finalUnitTitle")
                viewModel.loadFlashcards(deckId, userId, finalUnitTitle)
            }

            when (val state = uiState) {

                is UnitUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UnitUiState.Error -> {
                    Text(text = "Error: ${state.message}")
                }

                is UnitUiState.Success -> {
                    UnitManagerScreen(
                        unitTitle = unitTitle,
                        flashcards = state.flashcards,
                        onNavigateBack = { navController.popBackStack() },
                        onSaveFlashcard = { id, word, reading, meaning, img, audio ->
                            // Logic gọi API lưu thẻ (nên gọi qua viewModel)
                        },
                        onDeleteFlashcard = { id ->
                            // Logic gọi API xóa thẻ (nên gọi qua viewModel)
                        }
                    )
                }
            }
        }

        composable(Screen.Quiz.route) {
            // QuizScreen
        }

        composable(
            route = Screen.Flashcard.route,
            arguments = listOf(
                navArgument("deckId") { type = NavType.IntType },
                navArgument("unitId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("deckName") { type = NavType.StringType; nullable = true },
                navArgument("unitTitle") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId") ?: 0
            val unitId = backStackEntry.arguments?.getString("unitId")
            val deckName = backStackEntry.arguments?.getString("deckName") ?: ""
            val unitTitle = backStackEntry.arguments?.getString("unitTitle") ?: ""
            FlashcardScreen(
                navController = navController,
                deckId = deckId,
                unitId = unitId,
                deckName = deckName,
                unitTitle = unitTitle // Unit 01...
            )
        }

        composable(Screen.Favorites.route) {
            MainAppLayout(navController = navController, authManager = authManager) { _ ->
                // FavoritesScreen
            }
        }

        composable(Screen.Statistics.route) {
            MainAppLayout(navController = navController, authManager = authManager) { _ ->
                // StatisticsScreen
            }
        }

        composable(Screen.Settings.route) {
            MainAppLayout(navController = navController, authManager = authManager) { _ ->
                // SettingsScreen
            }
        }

        composable(Screen.About.route) {
            MainAppLayout(navController = navController, authManager = authManager) { _ ->
                // AboutScreen
            }
        }

        composable(Screen.Profile.route) {
            MainAppLayout(navController = navController, authManager = authManager) { _ ->
                // ProfileScreen
            }
        }
    }
}