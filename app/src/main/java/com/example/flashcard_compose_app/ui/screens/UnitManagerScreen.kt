package com.example.flashcard_compose_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flashcard_compose_app.domain.model.Flashcard
import com.example.flashcard_compose_app.ui.components.AddEditFlashcardDialog
import com.example.flashcard_compose_app.ui.components.FlashcardManagerItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitManagerScreen(
    unitTitle: String,
    flashcards: List<Flashcard>, // Tạm thời nhận List cứng, sau này ViewModel sẽ bơm dữ liệu vào đây
    onNavigateBack: () -> Unit,
    // Callback truyền dữ liệu (word, reading, meaning...) lên để gọi API lưu
    onSaveFlashcard: (id: Int?, word: String, reading: String, meaning: String, imagePath: String?, audioPath: String?) -> Unit,
    onDeleteFlashcard: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingFlashcard by remember { mutableStateOf<Flashcard?>(null) }

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(flashcards) { card ->
                FlashcardManagerItem(
                    flashcard = card,
                    onEditClick = { clickedCard ->
                        editingFlashcard = clickedCard
                        showDialog = true
                    },
                    onDeleteClick = { clickedCard ->
                        onDeleteFlashcard(clickedCard.id)
                    }
                )
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
    }
}

@Preview(showBackground = true) // Thêm showBackground để dễ nhìn viền
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
        unitTitle = "Từ vựng Unit 1",
        flashcards = sampleFlashcards,
        onNavigateBack = {},
        onSaveFlashcard = { _, _, _, _, _, _ -> },
        onDeleteFlashcard = {}
    )
}