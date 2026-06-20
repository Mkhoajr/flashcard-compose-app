package com.example.flashcard_compose_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flashcard_compose_app.ui.components.QuizScreenContent
import com.example.flashcard_compose_app.ui.viewmodels.AppViewModelProvider
import com.example.flashcard_compose_app.ui.viewmodels.QuizViewModel

@Composable
fun QuizScreen(
    navController: NavController,
    deckId: Int,
    unitTitle: String,
) {
    val viewModel: QuizViewModel = viewModel(factory = AppViewModelProvider.QuizFactory)

    val finalUnitTitle = remember(unitTitle) {
        unitTitle
            .replace(" ", "")
            .replace("Unit0", "Unit")
    }

    // Get data from Server/Local Database when screen launched
    LaunchedEffect(deckId, finalUnitTitle) {

        println("DEBUG QUIZ: Sending to Server -> deckId=$deckId, unitTitle=$finalUnitTitle")

        viewModel.loadQuizData(deckId, finalUnitTitle)
    }

    // State from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()
    val questions by viewModel.quizQuestions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return // Stop rendering the rest of the UI until loading is done
    }

    if (isFinished || questions.isEmpty()) {
        val correct by viewModel.correctCount.collectAsState()
        val incorrect by viewModel.incorrectCount.collectAsState()

        // Result Screen (Can replace later)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Quiz Finished!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Correct: $correct")
            Text("Incorrect: $incorrect")
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Go Back")
            }
        }
        return
    }

    // State when doing quiz (Not loading, Not finished, Have questions)
    val currentQuestion = questions[currentIndex]
    val totalQuestions = questions.size

    QuizScreenContent(
        currentQuestionIndex = currentIndex,
        totalQuestions = totalQuestions,
        questionText = currentQuestion.flashcard.word, // Display Vocab as question
        options = currentQuestion.options,             // 4 shuffle answers
        onOptionSelected = { selectedAnswer ->
            // Get User's select question to ViewModel to get Point
            viewModel.submitAnswer(selectedAnswer)
        },
        onNotKnowClick = {
            viewModel.skipQuestion()
        },
        onCloseClick = {
            navController.popBackStack()
        },
        onSettingsClick = {
        }
    )
}