package com.example.flashcard_compose_app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.flashcard_compose_app.data.AuthManager
import com.example.flashcard_compose_app.ui.components.MainAppLayout
import com.example.flashcard_compose_app.ui.screens.FlashcardScreen
import com.example.flashcard_compose_app.ui.screens.HomeScreen
import com.example.flashcard_compose_app.ui.screens.LoginScreen
import com.example.flashcard_compose_app.ui.screens.RegisterScreen

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
                    userName = authManager.getUserName() ?: "Unknow User",
                    modifier = Modifier.padding(innerPadding)
                )
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