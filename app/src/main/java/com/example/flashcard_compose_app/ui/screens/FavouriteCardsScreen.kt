package com.example.flashcard_compose_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flashcard_compose_app.ui.components.FavoriteFlashcardItem
import com.example.flashcard_compose_app.domain.model.Flashcard
import com.example.flashcard_compose_app.ui.components.RemoveFavoriteDialog
import com.example.flashcard_compose_app.ui.viewmodels.UnitUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteCardsScreen(
    uiState: UnitUiState,
    onNavigateBack: () -> Unit,
    onRemoveFavorite: (Flashcard) -> Unit,
    onStudyClick: () -> Unit
) {

    var showUnfavoriteDialog by remember { mutableStateOf(false) }
    var cardToUnfavorite by remember { mutableStateOf<Flashcard?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Favourite Cards", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        // STUDY NOW BUTTON - Only show if there are favorite cards available
//        floatingActionButton = {
//            if (uiState is UnitUiState.Success && uiState.flashcards.isNotEmpty()) {
//                ExtendedFloatingActionButton(
//                    onClick = onStudyClick,
//                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Study Now", tint = Color.White) },
//                    text = { Text("Study Now", color = Color.White, fontWeight = FontWeight.Bold) },
//                    containerColor = MaterialTheme.colorScheme.primary
//                )
//            }
//        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is UnitUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is UnitUiState.Error -> {
                    Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                }
                is UnitUiState.Success -> {
                    if (uiState.flashcards.isEmpty()) {
                        Text(
                            "You haven't added any favorite cards yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = uiState.flashcards,
                                key = { it.id }
                            ) { card ->
                                FavoriteFlashcardItem(
                                    flashcard = card,
                                    onRemoveFavorite = { clickedCard ->
                                        cardToUnfavorite = clickedCard
                                        showUnfavoriteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showUnfavoriteDialog && cardToUnfavorite != null) {
            RemoveFavoriteDialog(
                onConfirm = {
                    onRemoveFavorite(cardToUnfavorite!!)
                    showUnfavoriteDialog = false
                    cardToUnfavorite = null
                },
                onDismiss = {
                    showUnfavoriteDialog = false
                    cardToUnfavorite = null
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteCardsScreenPreview() {
    val sampleFlashcards = listOf(
        Flashcard(id = 1, word = "猫", reading = "ねこ", meaning = "Cat", imagePath = "", audioPath = "", unit = "", status = "", isFavourite= true),
        Flashcard(id = 2, word = "犬", reading = "いぬ", meaning = "Dog", imagePath = "", audioPath = "", unit = "", status = "", isFavourite = true),
        Flashcard(id = 3, word = "鳥", reading = "とり", meaning = "Bird", imagePath = "", audioPath = "", unit = "", status = "", isFavourite = true)
    )
    FavoriteCardsScreen(
        uiState = UnitUiState.Success(sampleFlashcards),
        onNavigateBack = {},
        onRemoveFavorite = {},
        onStudyClick = {}
    )
}