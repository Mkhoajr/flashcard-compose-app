package com.example.flashcard_compose_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AddDeckDialog(
    parentDeckName: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (title: String, isUnit: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var isUnit by remember { mutableStateOf(false) }
    val isSubDeck = parentDeckName != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isSubDeck) "Add to: $parentDeckName" else "Create Root Folder (Deck)",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isSubDeck) "Name (Folder or Lesson)" else "Folder Name (Deck)") },
                    placeholder = { Text("Enter Title")},
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Just display Checkbox choose "Unit/Lesson" if we're creating a sub-deck, since only sub-decks can be units
                if (isSubDeck) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isUnit,
                            onCheckedChange = { isUnit = it }
                        )
                        Text("This is Lesson (Contain Flashcard)", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, isUnit) },
                enabled = title.isNotBlank() // Just enable the button if there's some text input
            ) {
                Text("Create")
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
fun AddDeckDialogPreview() {
    AddDeckDialog(
        onDismiss = {},
        onConfirm = { title, isUnit -> /* Handle confirm */ }
    )
}