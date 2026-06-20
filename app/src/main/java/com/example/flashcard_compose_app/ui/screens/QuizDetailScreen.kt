package com.example.flashcard_compose_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flashcard_compose_app.ui.components.QuizDetailCard
import com.example.flashcard_compose_app.ui.theme.*
import com.example.flashcard_compose_app.ui.viewmodels.AppViewModelProvider
import com.example.flashcard_compose_app.ui.viewmodels.QuizHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizDetailScreen(
    navController: NavController,
    quizId: Int,
    modifier: Modifier = Modifier
) {
    val viewModel: QuizHistoryViewModel = viewModel(factory = AppViewModelProvider.QuizHistoryFactory)
    val detailsList by viewModel.selectedQuizDetails.collectAsState()
    val isLoading by viewModel.isDetailLoading.collectAsState()

    // Auto-load quiz details when screen is launched or quizId changes
    LaunchedEffect(quizId) {
        viewModel.loadQuizDetails(quizId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Review", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Indigo)
            )
        },
        containerColor = Grey50,
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Indigo
                )
            } else if (detailsList.isEmpty()) {
                Text(
                    text = "No details found.",
                    color = Grey500,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(detailsList) { detail ->
                        QuizDetailCard(detail = detail)
                    }
                }
            }
        }
    }
}