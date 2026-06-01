package com.example.flashcard_compose_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flashcard_compose_app.domain.model.Deck

@Composable
fun EditDeckDialog(
    deckToEdit: Deck,
    onDismiss: () -> Unit,
    onConfirm: (title: String, isUnit: Boolean) -> Unit
) {
    var title by remember { mutableStateOf(deckToEdit.title) }
    var isUnit by remember { mutableStateOf(deckToEdit.isUnit) }

    val isSubDeck = deckToEdit.parentId != 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Edit Deck / Unit", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Chỉ cho phép đổi trạng thái Bài học nếu không phải thư mục gốc
                if (isSubDeck) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isUnit,
                            onCheckedChange = { isUnit = it }
                        )
                        Text("This is a Unit (Contains Flashcards)", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, isUnit) },
                enabled = title.isNotBlank()
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