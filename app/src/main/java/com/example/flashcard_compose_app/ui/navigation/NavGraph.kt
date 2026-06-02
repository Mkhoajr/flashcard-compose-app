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
import com.example.flashcard_compose_app.ui.screens.FavoriteCardsScreen
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
                    userId = authManager.getUserId()?.toIntOrNull() ?: 0,
                    userName = authManager.getUserName() ?: "Unknown User",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        // Unit Manager Screen (for both regular units and virtual decks)
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

            // 🟢 1. ĐƯA userId RA NGOÀI ĐỂ DÙNG CHUNG CHO CẢ GET VÀ CRUD
            val userId = authManager.getUserId()?.toIntOrNull() ?: 0

            // 🟢 2. ĐƯA finalUnitTitle RA NGOÀI TƯƠNG TỰ
            val finalUnitTitle = if (isUnit) {
                unitTitle
                    .replace(" ", "")
                    .replace("Unit0", "Unit")
            } else {
                ""
            }

            LaunchedEffect(deckId) {
                println("DEBUG: UnitManager loading with deckId=$deckId, unitTitle=$unitTitle, isUnit=$isUnit")
                println("DEBUG: Gọi API với deckId=$deckId, DB Unit=$finalUnitTitle")

                viewModel.loadFlashcards(deckId, userId, finalUnitTitle)
            }

            UnitManagerScreen(
                unitTitle = unitTitle,
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() },

                onSaveFlashcard = { id, word, reading, meaning, img, audio ->
                    if (id == null) {
                        // NẾU ID = NULL -> TẠO MỚI (CREATE)
                        viewModel.addFlashcard(
                            deckId = deckId,
                            userId = userId,
                            unitTitle = finalUnitTitle,
                            word = word,
                            reading = reading,
                            meaning = meaning,
                            imagePath = img,
                            audioPath = audio
                        )
                    } else {
                        // NẾU CÓ ID -> CẬP NHẬT (UPDATE)
                        viewModel.updateFlashcard(
                            cardId = id,
                            deckId = deckId,
                            userId = userId,
                            unitTitle = finalUnitTitle,
                            word = word,
                            reading = reading,
                            meaning = meaning,
                            imagePath = img,
                            audioPath = audio
                        )
                    }
                },

                onDeleteFlashcard = { id ->
                    // (DELETE)
                    viewModel.deleteFlashcard(
                        cardId = id,
                        deckId = deckId,
                        userId = userId,
                        unitTitle = finalUnitTitle
                    )
                }
            )
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

        composable(route = Screen.Favorites.route) {
            val viewModel: UnitManagerViewModel = viewModel(factory = AppViewModelProvider.UnitManagerFactory)
            val uiState by viewModel.uiState.collectAsState()

            val context = LocalContext.current
            val authManager = AuthManager(context)
            val userId = authManager.getUserId()?.toIntOrNull() ?: 0

            LaunchedEffect(Unit) {
                viewModel.loadFavoriteFlashcards(userId)
            }

            FavoriteCardsScreen(
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() },
                onRemoveFavorite = { card ->
                    viewModel.toggleFavoriteStatus(cardId = card.id, userId = userId)

                    viewModel.loadFavoriteFlashcards(userId)
                },
                onStudyClick = {
                    // Điều hướng sang FlashcardScreen để học danh sách này.
                    // Có thể truyền một deckId ảo (ví dụ -1) để báo hiệu đây là list Favorites
                    // navController.navigate("flashcard_screen/-1?deckName=Favorites&unitTitle=Review")
                }
            )
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