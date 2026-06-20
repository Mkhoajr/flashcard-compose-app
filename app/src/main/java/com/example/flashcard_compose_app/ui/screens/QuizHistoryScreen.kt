package com.example.flashcard_compose_app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flashcard_compose_app.ui.components.QuizHistoryCard
import com.example.flashcard_compose_app.ui.navigation.Screen
import com.example.flashcard_compose_app.ui.theme.*
import com.example.flashcard_compose_app.ui.viewmodels.AppViewModelProvider
import com.example.flashcard_compose_app.ui.viewmodels.QuizHistoryViewModel

@Composable
fun QuizHistoryScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: QuizHistoryViewModel = viewModel(factory = AppViewModelProvider.QuizHistoryFactory)
    val historyList by viewModel.historyList.collectAsState()
    val isLoading by viewModel.isHistoryLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserHistory()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Grey50)
    ) {
        // --- HEADER ---
        Surface(
            color = Indigo,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.HistoryEdu,
                    contentDescription = "History",
                    tint = White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your Quiz History",
                    color = White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Track your learning progress",
                    color = IndigoLight,
                    fontSize = 14.sp
                )
            }
        }

        // --- BODY ---
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Indigo
                )
            } else if (historyList.isEmpty()) {
                Text(
                    text = "No quizzes taken yet.\nStart a quiz to see your history here!",
                    color = Grey500,
                    fontSize = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(historyList) { historyItem ->
                        QuizHistoryCard(
                            item = historyItem,
                            onClick = {
                                navController.navigate(Screen.QuizDetail.createRoute(historyItem.id))
                            }
                        )
                    }
                }
            }
        }
    }
}