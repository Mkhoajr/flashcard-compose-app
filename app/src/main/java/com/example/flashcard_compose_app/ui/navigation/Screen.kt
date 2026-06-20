package com.example.flashcard_compose_app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Flashcard : Screen("flashcard_screen/{deckId}?unitId={unitId}&deckName={deckName}&unitTitle={unitTitle}")
    object Quiz : Screen("quiz/{deckId}?unitTitle={unitTitle}") {
        fun createRoute(deckId: Int, unitTitle: String): String {
            return "quiz/$deckId?unitTitle=${android.net.Uri.encode(unitTitle)}"
        }
    }
    object QuizHistory : Screen("quiz_history")
    object QuizDetail : Screen("quiz_detail/{quizId}") {
        fun createRoute(quizId: Int): String {
            return "quiz_detail/$quizId"
        }
    }
    object Favorites : Screen("favorites")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object About : Screen("about")
    object Profile : Screen("profile")

    object UnitManager : Screen("unit_manager/{deckId}?unitTitle={unitTitle}&isUnit={isUnit}") {
        fun createRoute(deckId: Int?, unitTitle: String, isUnit: Boolean): String {
            return "unit_manager/$deckId?unitTitle=${android.net.Uri.encode(unitTitle)}&isUnit=$isUnit"
        }
    }
}