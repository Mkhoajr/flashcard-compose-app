package com.example.flashcard_compose_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flashcard_compose_app.domain.model.Flashcard

@Composable
fun AddEditFlashcardDialog(
    flashcardToEdit: Flashcard? = null,
    onDismiss: () -> Unit,
    onSave: (word: String, reading: String, meaning: String, imagePath: String?, audioPath: String?) -> Unit
) {
    val isEditMode = flashcardToEdit != null

    var word by remember { mutableStateOf(flashcardToEdit?.word ?: "") }
    var reading by remember { mutableStateOf(flashcardToEdit?.reading ?: "") }
    var meaning by remember { mutableStateOf(flashcardToEdit?.meaning ?: "") }
    var imagePath by remember { mutableStateOf(flashcardToEdit?.imagePath) }
    var audioPath by remember { mutableStateOf(flashcardToEdit?.audioPath) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditMode) "Edit Flashcard" else "Add New Flashcard",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = word,
                    onValueChange = { word = it },
                    label = { Text("Vocab") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reading,
                    onValueChange = { reading = it },
                    label = { Text("Reading") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = meaning,
                    onValueChange = { meaning = it },
                    label = { Text("Meaning") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Text("Attach", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Add Image Button
                    OutlinedButton(
                        onClick = {
                            // TODO: Mở thư viện ảnh (Photo Picker)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (!imagePath.isNullOrBlank()) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        )
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "Image", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (!imagePath.isNullOrBlank()) "Change Image" else "Add Image")
                    }

                    // Add Audio Button
                    OutlinedButton(
                        onClick = {
                            // TODO: Open audio recorder or picker
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (!audioPath.isNullOrBlank()) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        )
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Audio", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (!audioPath.isNullOrBlank()) "Change Audio" else "Record Audio")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(word, reading, meaning, imagePath, audioPath) },
                enabled = word.isNotBlank() && meaning.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun AddEditFlashcardDialogPreview() {
    AddEditFlashcardDialog(
        flashcardToEdit = Flashcard(
            id = 1,
            unit = "Unit 01",
            word = "猫",
            reading = "ねこ",
            meaning = "Cat",
            imagePath = null,
            audioPath = null,
            status = "not-learned"
        ),
        onDismiss = {},
        onSave = { _, _, _, _, _ -> }
    )
}