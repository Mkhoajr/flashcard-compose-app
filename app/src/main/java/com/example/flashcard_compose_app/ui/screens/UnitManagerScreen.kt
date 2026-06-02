package com.example.flashcard_compose_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flashcard_compose_app.domain.model.Flashcard
import com.example.flashcard_compose_app.ui.components.AddEditFlashcardDialog
import com.example.flashcard_compose_app.ui.components.DeleteConfirmationDialog
import com.example.flashcard_compose_app.ui.components.FlashcardManagerItem
import com.example.flashcard_compose_app.ui.viewmodels.UnitUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitManagerScreen(
    unitTitle: String,
    uiState: UnitUiState, // Pass the entire UI state instead of just flashcards
    onNavigateBack: () -> Unit,
    onSaveFlashcard: (id: Int?, word: String, reading: String, meaning: String, imagePath: String?, audioPath: String?) -> Unit,
    onDeleteFlashcard: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingFlashcard by remember { mutableStateOf<Flashcard?>(null) }
    var flashcardToDelete by remember { mutableStateOf<Flashcard?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Manage: $unitTitle", fontWeight = FontWeight.Bold) },
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

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingFlashcard = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Flashcard", tint = Color.White)
            }
        }
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
                            "No flashcards yet.",
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
                            items(uiState.flashcards) { card ->
                                FlashcardManagerItem(
                                    flashcard = card,
                                    onEditClick = { clickedCard ->
                                        editingFlashcard = clickedCard
                                        showDialog = true
                                    },
                                    onDeleteClick = { clickedCard ->
                                        flashcardToDelete = clickedCard
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dialog for Adding/Editing Flashcard
        if (showDialog) {
            AddEditFlashcardDialog(
                flashcardToEdit = editingFlashcard,
                onDismiss = {
                    showDialog = false
                    editingFlashcard = null
                },
                onSave = { word, reading, meaning, img, audio ->
                    onSaveFlashcard(editingFlashcard?.id, word, reading, meaning, img, audio)
                    showDialog = false
                    editingFlashcard = null
                }
            )
        }

        // Confirmation Dialog for Deletion
        if (flashcardToDelete != null) {
            DeleteConfirmationDialog(
                title = "Confirm Deletion",
                message = "Are you sure to Delete '${flashcardToDelete?.word}'? This action cannot be undone.",
                onConfirm = {
                    flashcardToDelete?.let { card ->
                        onDeleteFlashcard(card.id)
                    }
                    flashcardToDelete = null
                },
                onDismiss = {
                    flashcardToDelete = null
                }
            )
        }
    }
}

@Preview
@Composable
fun UnitManagerScreenPreview() {

    val sampleFlashcards = listOf(
        Flashcard(
            id = 1,
            unit = "Unit 01",
            word = "猫",
            reading = "ねこ",
            meaning = "Cat",
            imagePath = null,
            audioPath = null,
            status = "not-learned"
        ),
        Flashcard(
            id = 2,
            unit = "Unit 01",
            word = "犬",
            reading = "いぬ",
            meaning = "Dog",
            imagePath = null,
            audioPath = null,
            status = "not-learned"
        ),
        Flashcard(
            id = 3,
            unit = "Unit 01",
            word = "鳥",
            reading = "とり",
            meaning = "Bird",
            imagePath = null,
            audioPath = null,
            status = "not-learned"
        )
    )

    UnitManagerScreen(
        unitTitle = "Vocab Unit 1",
        uiState = UnitUiState.Success(sampleFlashcards),
        onNavigateBack = {},
        onSaveFlashcard = { _, _, _, _, _, _ -> },
        onDeleteFlashcard = {}
    )
}