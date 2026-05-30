package com.example.flashcard_compose_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flashcard_compose_app.domain.model.Flashcard

@Composable
fun FlashcardManagerItem(
    flashcard: Flashcard,
    onEditClick: (Flashcard) -> Unit,
    onDeleteClick: (Flashcard) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display flashcard details
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = flashcard.word,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (flashcard.reading.isNotBlank()) {
                    Text(
                        text = flashcard.reading,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = flashcard.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(onClick = { onEditClick(flashcard) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { onDeleteClick(flashcard) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Preview
@Composable
fun FlashcardManagerItemPreview() {
    FlashcardManagerItem(
        flashcard = Flashcard(
            id = 1,
            unit = "Unit 01",
            word = "猫",
            reading = "ねこ",
            meaning = "Cat",
            imagePath = null,
            audioPath = null,
            status = "not-learned"
        ),
        onEditClick = {},
        onDeleteClick = {}
    )
}